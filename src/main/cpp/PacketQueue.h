#include <mutex>
#include <condition_variable>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
	#include <libavutil/mem.h> // memory
}

#ifndef PACKET_QUEUE_H_
#define PACKET_QUEUE_H_

/**
 * Port of the packet queue from ffplay.c into c++
 *
 * Replaces the SDL mutex through c++ std mutex/condition variable
 */
class PacketQueue {
    private:
        // This list maintains the next for the queue
        typedef struct MyAVPacketList {
            AVPacket pkt;
            struct MyAVPacketList *next;
            int serial;
        } MyAVPacketList;

        int abort_request;
        int serial;
        int nb_packets;

        MyAVPacketList *first_pkt, *last_pkt;
        int size;
		int64_t duration;
        int condition;
        std::mutex mutex;
        std::condition_variable cond;

		int _put(AVPacket *pkt);

    public:
        AVPacket flush_pkt; // TODO(fraudies): better use one object for all queues

		PacketQueue();

		// Should we detroy the mutex and condition_variabe also ?
        virtual ~PacketQueue() {
            flush();
            av_packet_unref(&flush_pkt);
            av_freep(&flush_pkt);
        }

		void flush();

		void abort();

		inline int is_abort_request() const {
			return abort_request;
		}

		inline int* get_p_serial() { 
			return &serial;
		}

		inline int get_serial() const {
			return serial;
		}

		inline int get_size() const {
			return size;
		}

		inline int get_nb_packets() const {
			return nb_packets;
		}

		inline int64_t get_duration() const {
			return duration;
		}

		void start();

		int put(AVPacket *pkt);

		int put_null_packet(int stream_index);

		int put_flush_packet();

        inline bool is_flush_packet(const AVPacket & pkt) const{
            return pkt.data == flush_pkt.data;
        }

        /* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
		int get(AVPacket *pkt, int block, int *serial);
};

#endif PACKET_QUEUE_H_