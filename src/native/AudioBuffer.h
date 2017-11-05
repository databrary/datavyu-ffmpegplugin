extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswresample/swresample.h>
}

#include <atomic>
#include <mutex>
#include <condition_variable>

/** Maximum size of the audio queue. */
#define AUDIO_QUEUE_MAX_SIZE 128


/**
 * This audio buffer holds packets in a linked list of elements AVPacketList.
 * The audio buffer is thread safe.
 * The audio buffer has a maximum capacity.
 * The put and get methods can be called from different threads, e.g. one 
 * producing and one consuming.
 */
class AudioBuffer {

	/** Pointer to the first and last packet in the linked list. */
	AVPacketList *first_pkt, *last_pkt;

	/** Number of packets in the linked list. */
	int n_packets;

	/** Mutex to protect queue when concurrently putting and concurrent gets */
	std::mutex *mu;

	/** Condition variable to protect queue. */
	std::condition_variable *cv;

	/** Flushing the audio buffer means removing all packets and also all 
	    packets that are put while flushing are not added. */
	std::atomic<bool> flushing;

public:
	/**
	 * Constructs and audio buffer and sets the defaults.
	 */
	AudioBuffer() {
		first_pkt = last_pkt = nullptr;
		n_packets = 0;
		mu = new std::mutex;
		cv = new std::condition_variable;
		flushing = false; // initialize flushing to false!
	}

	/**
	 * Frees an audio buffer.
	 */
	virtual ~AudioBuffer() {
		flush();
		delete mu;
		delete cv;
	}

	/**
	 * Flushes all packets that are in the buffer. While flushing no packets are
	 * put to the buffer and instead directly freed.
	 */
	void flush() {
		AVPacketList *pkt, *pkt1;
		flushing = true;
		// Release the producer and the consumer
		cv->notify_all();
		std::unique_lock<std::mutex> locker(*mu);
		// Emties the buffer		
		for (pkt = first_pkt; pkt; pkt = pkt1) {
			pkt1 = pkt->next;
			av_packet_unref(&pkt->pkt);
			av_freep(&pkt);
		}
		// Resets the internal values
		last_pkt = first_pkt = nullptr;
		n_packets = 0;
		locker.unlock();
		// Done with flushing
		flushing = false;
	}

	/**
	 * Get the current size of the buffer.
	 */
	inline int size() const { return n_packets;	}

	/**
	 * Returns true if this buffer is empty otherwise false.
	 *
	 * Returns: True if empty otherwise false.
	 */
	inline bool empty() const { return n_packets == 0; }

	/**
	 * Puts a packet to the linked list. If this buffer is in the state of 
	 * flushing the packet is NOT put to the buffer.
	 *
	 * -- pkt The packet to put to the buffer.
	 * 
	 * Returns: 0 if the packet was put; otherwise -1.
	 */
	int put(AVPacket *pkt) {
		AVPacketList *pkt1;
		// Copy the packet if it has not been copied (see av_dup_packet)
		if (av_dup_packet(pkt) < 0) { 
			return -1; 
		}
		// Allocate space for a packet list element
		pkt1 = (AVPacketList*) av_malloc(sizeof(AVPacketList));
		if (!pkt1) { 
			return -1; 
		}
		// Set the internals 
		pkt1->pkt = *pkt;
		pkt1->next = nullptr;
		std::unique_lock<std::mutex> locker(*mu);
		// Wait if we have no more space except for when flushing
		cv->wait(locker, [this](){return (n_packets < AUDIO_QUEUE_MAX_SIZE) || flushing;});
		if (flushing) {
			av_free(pkt1);
		} else {
			if (!last_pkt) {
				first_pkt = pkt1;
			} else {
				last_pkt->next = pkt1;
			}
			last_pkt = pkt1;
			n_packets++;
		}
		locker.unlock();
		cv->notify_one();
		return 0;
	}

	/**
	 * Get the packet from the buffer.
	 *
	 * -- pkt The packet to put to the buffer.
	 *
	 * Returns: -1 when flushing, 1 if packet is returned otherwise 0
	 */
	int get(AVPacket *pkt) {
		AVPacketList *pkt1;
		int ret = 0;		
		std::unique_lock<std::mutex> locker(*mu);
		// Wait on packets being available with the exception of flushing
		cv->wait(locker, [this](){return (n_packets > 0) || flushing;});
		// Flushing?
		if (flushing) {
			ret = -1; // outputs silence
		} else {
			pkt1 = first_pkt;
			first_pkt = pkt1->next;

			if (!first_pkt) {
				last_pkt = nullptr;
			}

			n_packets--;
			*pkt = pkt1->pkt;
			av_free(pkt1);
			ret = 1;	
		}
		// Unlock
		locker.unlock();
		cv->notify_one();
		return ret;	
	}
};
