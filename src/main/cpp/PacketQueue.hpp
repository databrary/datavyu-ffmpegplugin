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

        int _put(AVPacket *pkt) {
            MyAVPacketList *pkt1;

            if (abort_request)
            return -1;

            pkt1 = (struct MyAVPacketList *) av_malloc(sizeof(MyAVPacketList));
            if (!pkt1)
                return -1;
            pkt1->pkt = *pkt;
            pkt1->next = NULL;
            if (pkt == &flush_pkt)
                serial++;
            pkt1->serial = serial;

            if (!last_pkt)
                first_pkt = pkt1;
            else
                last_pkt->next = pkt1;
            last_pkt = pkt1;
            nb_packets++;
            size += pkt1->pkt.size + sizeof(*pkt1);
            duration += pkt1->pkt.duration;
            /* XXX: should duplicate packet data in DV case */
            condition = 1;
            cond.notify_all();
            return 0;
        }

    public:
        AVPacket flush_pkt; // TODO(fraudies): better use one object for all queues

        PacketQueue() :
            abort_request(1),
            serial(0),
            nb_packets(0),
            first_pkt(nullptr),
            last_pkt(nullptr),
            size(0),
            duration(0),
            condition(0) {

            av_init_packet(&flush_pkt);
            flush_pkt.data = (uint8_t *)&flush_pkt;
        }

		// Should we detroy the mutex and condition_variabe also ?
        virtual ~PacketQueue() {
            flush();
            av_packet_unref(&flush_pkt);
            av_freep(&flush_pkt);
        }

        void flush() {
            MyAVPacketList *pkt, *pkt1;

            std::unique_lock<std::mutex> locker(mutex);
            for (pkt = first_pkt; pkt; pkt = pkt1) {
                pkt1 = pkt->next;
                av_packet_unref(&pkt->pkt);
                av_freep(&pkt);
            }
            last_pkt = NULL;
            first_pkt = NULL;
            nb_packets = 0;
            size = 0;
            duration = 0;
            locker.unlock();
        }

        void abort() {
            std::unique_lock<std::mutex> locker(mutex);

            abort_request = 1;

            condition = 1;
            cond.notify_all();

            locker.unlock();
        }

		inline int is_abort_request() const {
			return abort_request;
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

        void start() {
            std::unique_lock<std::mutex> locker(mutex);
            abort_request = 0;
            _put(&flush_pkt);
            locker.unlock();
        }

        int put(AVPacket *pkt) {
            int ret;

            std::unique_lock<std::mutex> locker(mutex);
            ret = _put(pkt);
            locker.unlock();

            if (pkt != &flush_pkt && ret < 0)
                av_packet_unref(pkt);

            return ret;
        }

        int put_null_packet(int stream_index) {
            AVPacket pkt1, *pkt = &pkt1;
            av_init_packet(pkt);
            pkt->data = NULL;
            pkt->size = 0;
            pkt->stream_index = stream_index;
            return put(pkt);
        }

        int put_flush_packet() {
            return put(&flush_pkt);
        }

        inline bool is_flush_packet(AVPacket *pkt) const {
            return pkt->data == flush_pkt.data;
        }

        /* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
        int get(AVPacket *pkt, int block, int *serial) {
            MyAVPacketList *pkt1;
            int ret;

            std::unique_lock<std::mutex> locker(mutex);

            for (;;) {
                if (abort_request) {
                    ret = -1;
                    break;
                }

                pkt1 = first_pkt;
                if (pkt1) {
                    first_pkt = pkt1->next;
                    if (!first_pkt)
                        last_pkt = NULL;
                    nb_packets--;
                    size -= pkt1->pkt.size + sizeof(*pkt1);
                    duration -= pkt1->pkt.duration;
                    *pkt = pkt1->pkt;
                    if (serial)
                        *serial = pkt1->serial;
                    av_free(pkt1);
                    ret = 1;
                    break;
                } else if (!block) {
                    ret = 0;
                    break;
                } else {
                    condition = 0;
                    cond.wait(locker, [&]{ return condition; });
                }
            }
            locker.unlock();
            return ret;
        }
};

#endif PACKET_QUEUE_H_