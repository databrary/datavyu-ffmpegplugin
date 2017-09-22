#include <string>
#include <sstream>
#include <iomanip>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libavutil/opt.h>
	#include <libavutil/avstring.h>
}

#define BUFFER_SIZE 4096 

// Logging from
// https://github.com/FFmpeg/FFmpeg/blob/release/3.2/libavformat/dump.c
// The change here is that I log into a char buffer and return the message as 
// string.
static std::string log_metadata(void *ctx, AVDictionary *m, const char *indent) {
	char buffer[BUFFER_SIZE];
	int nBuffer = 0;
    if (m && !(av_dict_count(m) == 1 && av_dict_get(m, "language", NULL, 0))) {
        AVDictionaryEntry *tag = nullptr;

		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%sMetadata:\n", indent);
        while ((tag = av_dict_get(m, "", tag, AV_DICT_IGNORE_SUFFIX)))
            if (strcmp("language", tag->key)) {
                const char *p = tag->value;
				nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s  %-16s: ", 
									indent, tag->key);
                while (*p) {
                    char tmp[256];
                    size_t len = strcspn(p, "\x8\xa\xb\xc\xd");
                    av_strlcpy(tmp, p, FFMIN(sizeof(tmp), len+1));
					nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", tmp);
                    p += len;
					if (*p == 0xd) nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " ");
					if (*p == 0xa) nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "\n%s  %-16s: ", indent, "");
                    if (*p) p++;
                }
				nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "\n");
            }
    }
	return std::string(buffer);
}

static std::string log_fps(double d, const char *postfix) {
	char buffer[BUFFER_SIZE];
    uint64_t v = lrintf(d * 100);
    if (!v)
        //pLogger->info("%1.4f %s", d, postfix);
		snprintf(buffer, BUFFER_SIZE, "%1.4f %s", d, postfix);
    else if (v % 100)
        //pLogger->info("%3.2f %s", d, postfix);
		snprintf(buffer, BUFFER_SIZE, "%3.2f %s", d, postfix);
    else if (v % (100 * 1000))
        //pLogger->info("%1.0f %s", d, postfix);
		snprintf(buffer, BUFFER_SIZE, "%1.0f %s", d, postfix);
    else
        //pLogger->info("%1.0fk %s", d / 1000, postfix);
		snprintf(buffer, BUFFER_SIZE, "%1.0fk %s", d / 1000, postfix);
	return std::string(buffer);
}

static std::string log_stream_format(AVFormatContext *ic, int i, int index, int is_output) {
	char buffer[BUFFER_SIZE];
    char buf[256];
	int nBuffer = 0;
    int flags = (is_output ? ic->oformat->flags : ic->iformat->flags);
    AVStream *st = ic->streams[i];
    AVDictionaryEntry *lang = av_dict_get(st->metadata, "language", NULL, 0);
    char *separator = (char*)ic->dump_separator;
    AVCodecContext *avctx;
    int ret;

    avctx = avcodec_alloc_context3(NULL);
    if (!avctx)
		return std::string(buffer);

    ret = avcodec_parameters_to_context(avctx, st->codecpar);
    if (ret < 0) {
        avcodec_free_context(&avctx);
		return std::string(buffer);
    }

    // Fields which are missing from AVCodecParameters need to be taken from the AVCodecContext
    avctx->properties = st->codec->properties;
    avctx->codec      = st->codec->codec;
    avctx->qmin       = st->codec->qmin;
    avctx->qmax       = st->codec->qmax;
    avctx->coded_width  = st->codec->coded_width;
    avctx->coded_height = st->codec->coded_height;

    if (separator)
        av_opt_set(avctx, "dump_separator", separator, 0);
    avcodec_string(buf, sizeof(buf), avctx, is_output);
    avcodec_free_context(&avctx);

	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "    Stream #%d:%d", index, i);

    /* the pid is an important information, so we display it */
    /* XXX: add a generic system */
    if (flags & AVFMT_SHOW_IDS)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "[0x%x]", st->id);
    if (lang)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "(%s)", lang->value);

	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, ", %d, %d/%d", 
				st->codec_info_nb_frames, st->time_base.num, st->time_base.den);
	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, ": %s", buf);

    if (st->sample_aspect_ratio.num &&
        av_cmp_q(st->sample_aspect_ratio, st->codecpar->sample_aspect_ratio)) {
        AVRational display_aspect_ratio;
        av_reduce(&display_aspect_ratio.num, &display_aspect_ratio.den,
                  st->codecpar->width  * (int64_t)st->sample_aspect_ratio.num,
                  st->codecpar->height * (int64_t)st->sample_aspect_ratio.den,
                  1024 * 1024);
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, ", SAR %d:%d DAR %d:%d", 
			st->sample_aspect_ratio.num, st->sample_aspect_ratio.den,
            display_aspect_ratio.num, display_aspect_ratio.den);
    }

    if (st->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
        int fps = st->avg_frame_rate.den && st->avg_frame_rate.num;
        int tbr = st->r_frame_rate.den && st->r_frame_rate.num;
        int tbn = st->time_base.den && st->time_base.num;
        int tbc = st->codec->time_base.den && st->codec->time_base.num;

        if (fps || tbr || tbn || tbc)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", separator);

        if (fps)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
				log_fps(av_q2d(st->avg_frame_rate), tbr || tbn || tbc ? "fps, " : "fps").c_str());
        if (tbr)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
				log_fps(av_q2d(st->r_frame_rate), tbn || tbc ? "tbr, " : "tbr").c_str());
        if (tbn)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
				log_fps(1 / av_q2d(st->time_base), tbc ? "tbn, " : "tbn").c_str());
        if (tbc)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
				log_fps(1 / av_q2d(st->codec->time_base), "tbc").c_str());
    }

    if (st->disposition & AV_DISPOSITION_DEFAULT)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (default)");
    if (st->disposition & AV_DISPOSITION_DUB)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (dub)");
    if (st->disposition & AV_DISPOSITION_ORIGINAL)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (original)");
    if (st->disposition & AV_DISPOSITION_COMMENT)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (comment)");
    if (st->disposition & AV_DISPOSITION_LYRICS)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (lyrics)");
    if (st->disposition & AV_DISPOSITION_KARAOKE)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (karaoke)");
    if (st->disposition & AV_DISPOSITION_FORCED)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (forced)");
    if (st->disposition & AV_DISPOSITION_HEARING_IMPAIRED)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (hearing impaired)");
    if (st->disposition & AV_DISPOSITION_VISUAL_IMPAIRED)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (visual impaired)");
    if (st->disposition & AV_DISPOSITION_CLEAN_EFFECTS)
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, " (clean effects)");

	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "\n");
	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
						log_metadata(NULL, st->metadata, "    ").c_str());

	return std::string(buffer);
}

static std::string log_av_format(AVFormatContext *ic, int index, const char *filename, 
						  int is_output) {
	char buffer[BUFFER_SIZE];
	int nBuffer = 0;
	int i;
    uint8_t *printed = ic->nb_streams ? (uint8_t*)av_mallocz(ic->nb_streams) : NULL;
    if (ic->nb_streams && !printed)
		return  std::string(buffer);

	nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s #%d, %s, %s '%s':\n",
						is_output ? "Output" : "Input",
						index,
						is_output ? ic->oformat->name : ic->iformat->name,
						is_output ? "to" : "from", filename);
    nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
						log_metadata(NULL, ic->metadata, "  ").c_str());

    if (!is_output) {
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "  Duration: ");
        if (ic->duration != AV_NOPTS_VALUE) {
            int hours, mins, secs, us;
            int64_t duration = ic->duration + (ic->duration <= INT64_MAX - 5000 ? 5000 : 0);
            secs  = duration / AV_TIME_BASE;
            us    = duration % AV_TIME_BASE;
            mins  = secs / 60;
            secs %= 60;
            hours = mins / 60;
            mins %= 60;
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
								"%02d:%02d:%02d.%02d", hours, mins, 
				secs, (100 * us) / AV_TIME_BASE);
        } else {
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "N/A");
		}
        if (ic->start_time != AV_NOPTS_VALUE) {
            int secs, us;
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, ", start: ");
            secs = llabs(ic->start_time / AV_TIME_BASE);
            us   = llabs(ic->start_time % AV_TIME_BASE);
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s%d.%06d",
				(ic->start_time >= 0) ? "" : "-", secs,
				(int) av_rescale(us, 1000000, AV_TIME_BASE));
        }
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, ", bitrate: ");
        if (ic->bit_rate)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
								"%I64d kb/s", (int64_t)ic->bit_rate / 1000);
        else
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "N/A");
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "\n");
    }

    for (i = 0; i < ic->nb_chapters; i++) {
        AVChapter *ch = ic->chapters[i];
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
							"    Chapter #%d:%d: ", index, i);
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
							"start %f, ", ch->start * av_q2d(ch->time_base));
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
							"end %f\n", ch->end * av_q2d(ch->time_base));
		nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, 
						"%s", log_metadata(NULL, ch->metadata, "    ").c_str());
    }

    if (ic->nb_programs) {
        int j, k, total = 0;
        for (j = 0; j < ic->nb_programs; j++) {
            AVDictionaryEntry *name = av_dict_get(ic->programs[j]->metadata,
                                                  "name", NULL, 0);
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "  Program %d %s\n", 
				ic->programs[j]->id, name ? name->value : "");
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
				log_metadata(NULL, ic->programs[j]->metadata, "    ").c_str());
            for (k = 0; k < ic->programs[j]->nb_stream_indexes; k++) {
				nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s", 
							log_stream_format(ic, ic->programs[j]->stream_index[k],
												index, is_output).c_str());
                printed[ic->programs[j]->stream_index[k]] = 1;
            }
            total += ic->programs[j]->nb_stream_indexes;
        }
        if (total < ic->nb_streams)
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "  No Program\n");
    }

    for (i = 0; i < ic->nb_streams; i++)
        if (!printed[i])
			nBuffer += snprintf(buffer+nBuffer, BUFFER_SIZE, "%s",
								log_stream_format(ic, i, index, is_output).c_str());
    av_free(printed);
	return std::string(buffer);
}
