require 'Datavyu_API.rb'

#2014-09-03 - Jon Coe 
#chat export script. datavyu .opf -> childes/clan .cha
#convert the currently open spreadsheet to chat transcription, write it out to file

begin
	#USER CAN CHANGE VALUES BETWEEN HERE...
	metadata_col = "CHILDES_HEADER"
	source_col = "SPEECH"
	speaker_code = "speaker"
	content_code = "content"
	combined_code = "code01" #if this is empty string use speaker/content separately. otherwise this represents both
	output_file = $pj.getProjectName()+".cha"
	top_lines = ["@UTF8", "@Begin"] #go at the top of every file. not dependent on spreadsheet
	#... AND HERE

	output = File.open(output_file, "w")
	puts "--writing to '" + output_file + "' --"

	#metadata: the exact contents of each code of the first cell, in order
	top_lines.each do |x|
		puts x + "\n"
		output << x + "\n"
	end 
	mcol = getColumn(metadata_col)
	for i in mcol.cells[0].arglist
		current_str = mcol.cells[0].send(i.to_sym)
		#current_str.sub(":", ":\t") #to insert a tab after the colon uncomment this
		puts current_str + "\n"
		output << current_str + "\n"
	end

	#data: iterating thru the cells of source_col
	nak = 21.chr
	col = getColumn(source_col)
	for cur in col.cells
		if combined_code != ""
			full_line = cur.send(combined_code.to_sym)
			speaker_tok = full_line.partition(" ")[0]
			content_tok = full_line.partition(" ")[2]
		else
			speaker_tok = cur.send(speaker_code.to_sym)
			content_tok = cur.send(content_code.to_sym)
		end
		current_str = "*"+speaker_tok + ":\t" + content_tok + " " + nak + cur.onset.to_s + "_" + cur.offset.to_s + nak + "\n"
		puts current_str
		output << current_str
	end
	output << "@End"
	puts "@End"
	output.close
end
