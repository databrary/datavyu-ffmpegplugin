extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswresample/swresample.h>
}

#include <mutex> // std::mutex
#include <condition_variable>  // std::condition_variable


#define AUDIO_QUEUE_MAX_SIZE 128

class AudioBuffer {
  AVPacketList *first_pkt, *last_pkt;
  int nb_packets;
  int size;
  bool getPkt; // false
  std::mutex *mu;
  std::condition_variable *cv;
  int quit;
  int do_flush;
public:
  AudioBuffer() {
	  first_pkt = last_pkt = nullptr;
	  nb_packets = size = 0;
	  getPkt = false;
	  quit = 0;
	  mu = new std::mutex;
	  cv = new std::condition_variable;
  }
  virtual ~AudioBuffer() {
	flush();
	delete mu;
	delete cv;
  }
  void flush() {
    AVPacketList *pkt, *pkt1;
	do_flush = 1; // this releases the producer and the consumer
	std::unique_lock<std::mutex> locker(*mu);
    for (pkt = first_pkt; pkt; pkt = pkt1) {
        pkt1 = pkt->next;
        av_packet_unref(&pkt->pkt);
        av_freep(&pkt);
    }
    last_pkt = nullptr;
    first_pkt = nullptr;
    nb_packets = 0;
    size = 0;
	do_flush = 0;
	locker.unlock();
  }
  inline bool empty() const {
	return nb_packets == 0;
  }
  void stop() {
	quit = 1;
  }
  int put(AVPacket *pkt) {
	AVPacketList *pkt1;
	if (quit == 1) { return -1; }
	if (av_dup_packet(pkt) < 0) { return -1; }
	pkt1 = (AVPacketList*) av_malloc(sizeof(AVPacketList));
	if (!pkt1) { return -1; }
	pkt1->pkt = *pkt;
	pkt1->next = nullptr;
	std::unique_lock<std::mutex> locker(*mu);
	cv->wait(locker, [this](){return (nb_packets < AUDIO_QUEUE_MAX_SIZE) 
		|| (do_flush == 1);});
	if (do_flush == 1) {
		av_free(pkt1);
	} else {
		if (!last_pkt)
			first_pkt = pkt1;
		else
			last_pkt->next = pkt1;

		last_pkt = pkt1;
		nb_packets++;
		size += pkt1->pkt.size;	
	}
	locker.unlock();
	cv->notify_one();
	return 0;
  }
  int get(AVPacket *pkt) {
	AVPacketList *pkt1;
	int ret = 0;

	if (quit) { return -1; }

	std::unique_lock<std::mutex> locker(*mu);
    cv->wait(locker, [this](){return (nb_packets > 0) || (do_flush == 1);});

	if (do_flush == 1) {
		ret = -1; // puts silence
	} else {
		pkt1 = first_pkt;
		first_pkt = pkt1->next;

		if (!first_pkt)
			last_pkt = nullptr;

		nb_packets--;
		size -= pkt1->pkt.size;
		*pkt = pkt1->pkt;
		av_free(pkt1);
		ret = 1;	
	}

	locker.unlock();
	cv->notify_one();

	return ret;	
  }
};
