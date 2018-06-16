#include <mutex>
#include <condition_variable>

extern "C" {
	#include <libavcodec/avcodec.h> // codecs
    #include <libavutil/error.h> // error codes
}

#ifndef AV_PACKET_QUEUE_H_
#define AV_PACKET_QUEUE_H_

// TODO(fraudies): Convert this into a C++ class
// Note, I've replaced the SDL mutex through c++ std mutex/condition_variable

static AVPacket flush_pkt;

class PacketQueue{
    private:
        // This list maintains the next for the queue
        typedef struct MyAVPacketList {
            AVPacket pkt;
            struct MyAVPacketList *next;
            int serial;
        } MyAVPacketList;
        
        static int packet_queue_put_private(PacketQueue *q, AVPacket *pkt)
        {
            MyAVPacketList *pkt1;

            if (q->abort_request)
            return -1;

            pkt1 = (struct MyAVPacketList *)av_malloc(sizeof(MyAVPacketList));
            if (!pkt1)
                return -1;
            pkt1->pkt = *pkt;
            pkt1->next = NULL;
            if (pkt == &flush_pkt)
                q->serial++;
            pkt1->serial = q->serial;

            if (!q->last_pkt)
                q->first_pkt = pkt1;
            else
                q->last_pkt->next = pkt1;
            q->last_pkt = pkt1;
            q->nb_packets++;
            q->size += pkt1->pkt.size + sizeof(*pkt1);
            q->duration += pkt1->pkt.duration;
            /* XXX: should duplicate packet data in DV case */
            q->condition = 1;
            q->cond->notify_all();
            return 0;
        }

    public:
        PacketQueue(){}

        int abort_request;
        int serial;
        int nb_packets;
        
        MyAVPacketList *first_pkt, *last_pkt;
        int size;
        int64_t duration;
        std::mutex *mutex;
        std::condition_variable* cond;
        int condition;

        /* packet queue handling */
        static int packet_queue_init(PacketQueue *q)
        {
            memset(q, 0, sizeof(PacketQueue));
            q->mutex = new std::mutex();
            if (!q->mutex) {
                // TODO: Add some logging back
                //av_log(NULL, AV_LOG_FATAL, "SDL_CreateMutex(): %s\n", SDL_GetError());
                return AVERROR(ENOMEM);
            }
            q->cond = new std::condition_variable();
            if (!q->cond) {
                // TODO: add some logging back
                // av_log(NULL, AV_LOG_FATAL, "SDL_CreateCond(): %s\n", SDL_GetError());
                return AVERROR(ENOMEM);
            }
            q->abort_request = 1;
            return 0;
        }

        static void packet_queue_destroy(PacketQueue *q)
        {
            packet_queue_flush(q);
            delete q->mutex;
            delete q->cond;
        }


        static int packet_queue_put(PacketQueue *q, AVPacket *pkt)
        {
            int ret;

            std::unique_lock<std::mutex> locker(*q->mutex);
            ret = packet_queue_put_private(q, pkt);
            locker.unlock();

            if (pkt != &flush_pkt && ret < 0)
                av_packet_unref(pkt);

            return ret;
        }

        static int packet_queue_put_nullpacket(PacketQueue *q, int stream_index)
        {
            AVPacket pkt1, *pkt = &pkt1;
            av_init_packet(pkt);
            pkt->data = NULL;
            pkt->size = 0;
            pkt->stream_index = stream_index;
            return packet_queue_put(q, pkt);
        }

        static void packet_queue_flush(PacketQueue *q)
        {
            MyAVPacketList *pkt, *pkt1;

            std::unique_lock<std::mutex> locker(*q->mutex);
            for (pkt = q->first_pkt; pkt; pkt = pkt1) {
                pkt1 = pkt->next;
                av_packet_unref(&pkt->pkt);
                av_freep(&pkt);
            }
            q->last_pkt = NULL;
            q->first_pkt = NULL;
            q->nb_packets = 0;
            q->size = 0;
            q->duration = 0;
            locker.unlock();
        }

        static void packet_queue_abort(PacketQueue *q)
        {
            std::unique_lock<std::mutex> locker(*q->mutex);

            q->abort_request = 1;

            q->condition = 1;
            q->cond->notify_all();

            locker.unlock();
        }

        static void packet_queue_start(PacketQueue *q)
        {
            std::unique_lock<std::mutex> locker(*q->mutex);
            q->abort_request = 0;
            packet_queue_put_private(q, &flush_pkt);
            locker.unlock();
        }

        /* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
        static int packet_queue_get(PacketQueue *q, AVPacket *pkt, int block, int *serial)
        {
            MyAVPacketList *pkt1;
            int ret;

            std::unique_lock<std::mutex> locker(*q->mutex);

            for (;;) {
                if (q->abort_request) {
                    ret = -1;
                    break;
                }

                pkt1 = q->first_pkt;
                if (pkt1) {
                    q->first_pkt = pkt1->next;
                    if (!q->first_pkt)
                        q->last_pkt = NULL;
                    q->nb_packets--;
                    q->size -= pkt1->pkt.size + sizeof(*pkt1);
                    q->duration -= pkt1->pkt.duration;
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
                    q->condition = 0;
                    q->cond->wait(locker, [&q]{return q->condition;});
                    //SDL_CondWait(q->cond, q->mutex);
                }
            }
            locker.unlock();
            return ret;
        }

};

#endif AV_PACKET_QUEUE_H_