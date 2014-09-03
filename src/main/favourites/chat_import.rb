require 'Datavyu_API.rb'

#2014-09-03 jc
#chat import script. childes/clan .cha -> datavyu .opf
#add two columns to the currently open spreadsheet. content determined by input .cha
#unrelated spreadsheet contents should remain unchanged

begin
	#USER CAN CHANGE VALUES BETWEEN HERE...
	metadata_col = "CHILDES_HEADER"
	source_col = "SPEECH"
	speaker_code = "speaker"
	content_code = "content"
	combined_code = "code01" #if this is empty string use speaker/content separately. otherwise this represents both
	input_file = "Z:\\datavyu\\target\\datavyu-childes-template.cha"
	#... AND HERE

	puts "--reading from '" + input_file + "'--"

	#create necessary columns
	header = createNewColumn(metadata_col)
	header.make_new_cell()
	header_count = 0
	if combined_code != ""
		source = createNewColumn(source_col, combined_code)
	elsif
		source = createNewColumn(source, speaker_code, content_code)
	end

	nak = 21.chr
	#scan file and populate
	File.readlines(input_file).each do |cur_line|
		#header lines start with @
		if cur_line[0] == "@"
			header_count += 1
			add_codes_to_column(header, "code"+header_count)
			#header.cells[0].instance_variable_set("code"+header_count, cur_line)
			header.cells[0].
			
		#content lines start with *
		elsif cur_line[0] == "*"
			times = cur_line.partition(nak)[2]
			t_onset = times.partition("_")[0]
			t_offset = times.partition("_")[2].partition(nak)[0]
			source.make_new_cell()
			source.cells.last.onset = t_onset
			source.cells.last.offset = t_offset	
			if combined_code != ""
				source.cells.last.instance_variable_set(combined_code, cur_line.partition(nak)[0])
			else
				source.cells.last.instance_variable_set(speaker_code, cur_line.partition("\t")[0])
				source.cells.last.instance_variable_set(content_code, cur_line.partition(nak)[0].partition("\t")[2])
			end
		end
		#lines beginning with anything other than @ and * will be ignored
	end
	setVariable(metadata_col, header)
	setVariable(source_col, source) 
end 
