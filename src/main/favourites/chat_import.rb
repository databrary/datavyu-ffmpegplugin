require 'Datavyu_API.rb'

#2014-09-03 jc
#chat import script. childes/clan .cha -> datavyu .opf
#add two columns to the currently open spreadsheet. content determined by input .cha
#unrelated spreadsheet contents should remain unchanged

begin
	#USER CAN CHANGE VALUES BETWEEN HERE...
	metadata_col = "CHILDES_HEADER"
	speech_col = "SPEECH"
	speaker_code = "speaker"
	content_code = "content"
	combined_code = "" #if this is empty string use speaker/content separately. otherwise this represents both
	input_file = $path+"favourites"+File::SEPARATOR+"datavyu-childes-template.cha"
	skip_fields_before_begin = true
	#... AND HERE
	begin_happened = false


	puts "--reading from '" + input_file + "'--"
	#create necessary columns
	#header
	header = createNewColumn(metadata_col)
	header_count = 0
	header_hash = Hash.new
	#speech
	if combined_code != ""
		speech = createNewColumn(speech_col, combined_code)
	elsif
		speech = createNewColumn(speech_col, speaker_code, content_code)
	end

	nak = 21.chr
	#scan file and populate
	File.readlines(input_file).each do |cur_line|
		#header lines start with @
		if cur_line.index("@") == 0 && (!skip_fields_before_begin || begin_happened) && cur_line != "@End"
			header_count += 1
			header.add_code("code"+header_count.to_s)
			header_hash["code"+header_count.to_s] = cur_line.strip
		#content lines start with *
		elsif cur_line.index("*") == 0
			times = cur_line.partition(nak)[2]
			content = cur_line.partition(nak)[0]
			t_onset = times.partition("_")[0]
			t_offset = times.partition("_")[2].partition(nak)[0]
			speech.make_new_cell()
			speech.cells.last.onset = t_onset
			speech.cells.last.offset = t_offset	
			if combined_code != ""
				speech.cells.last.change_arg(combined_code, content.sub("\t", " "))
			else
				speech.cells.last.change_arg(speaker_code, content.partition(":\t")[0])
				speech.cells.last.change_arg(content_code, content.partition(":\t")[2])
			end
		elsif cur_line.index("@Begin") == 0
			begin_happened = true
		end
		#lines beginning with anything other than @ and * will be ignored
	end
	header.make_new_cell()
	Hash[header_hash.sort].each_pair do |k, v|
		header.cells[0].change_arg(k,v)
	end
	setVariable(header)
	setVariable(speech) 
end 
