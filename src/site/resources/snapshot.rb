#!/usr/bin/env ruby

require 'rubygems'
require 'hpricot'
require 'net/http'
require 'uri'
require 'cgi'
require 'date'
load 'snapshot-config.rb'

downloads = Array.new
Dir.foreach($DOWNLOAD_DIR) do |file|
  unless file == "." or file == ".."
    name_chunks = file.split("-")

    download = {
        :version => name_chunks[$VERSION_CHUNK],
        :date => Date.parse(name_chunks[$TIME_CHUNK].split(".")[0]),
        :file => file
    }

    downloads.push(download)
  end
end

# Sort the downloads by the date they were added to the server.
downloads = downloads.sort do |x, y|
  y[:date] <=> x[:date]
end

# The snapshot release is the working version - i.e. it is the very latest build.
# The official release is the first version that changes from the snapshot release.
snapshot_release = downloads[0]
official_release = nil
snapshot_downloads = Array.new
downloads.each do |download|
  unless download[:date] != snapshot_release[:date]
    snapshot_downloads.push(download)
    downloads.delete(download)
  end

  if download[:version] != snapshot_release[:version]
    official_release = download
    break
  end
end

if official_release.nil?
  official_release = snapshot_release
  official_release[:date] = Date.parse("20120101")
end

resource_uri = URI.parse("http://www.pivotaltracker.com/services/v3/projects/#{$PROJECT_ID}/stories?filter=modified_since%3A#{official_release[:date]}%20includedone%3Atrue%20state%3Afinished,delivered,accepted")
response = Net::HTTP.start(resource_uri.host, resource_uri.port) do |http|
  http.get(resource_uri.to_s, {'X-TrackerToken' => $TOKEN})
end

doc = Hpricot(response.body).at('stories')

fixed_stories = Array.new()
completed_stories = Array.new()
(doc/:story).each do |story|
  pivotal_story = {
    :name => (story/:name).inner_html,
    :url => (story/:url).inner_html
  }

  if pivotal_story[:name].length > $SUMMARY_LENGTH
    pivotal_story[:name] = pivotal_story[:name][0, $SUMMARY_LENGTH] + "..."
  end

  if (story/:current_state).inner_html == "accepted"
     completed_stories.push(pivotal_story)
  end

  if (story/:current_state).inner_html == "finished" or (story/:current_state).inner_html == "delivered" or
     fixed_stories.push(pivotal_story)
  end

end

puts <<EOS
Content-type: text/html

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<title>OpenSHAPA - Discovering more, faster</title>
  		<link href="openshapa.css" media="screen" rel="stylesheet" type="text/css" />
  		<!--[if lte IE 6]>
  		  	<link href="openshapa-ie6.css" media="screen" rel="stylesheet" type="text/css" />
  		  	<script defer type="text/javascript" src="js/pngfix.js"></script>
  		  	<script type="text/javascript" src="js/sizzle.js"></script>
  		<![endif]-->
  		<script type="text/javascript" src="js/cufon-yui.js"></script>
  		<script type="text/javascript" src="js/Museo_300-Museo_400.font.js"></script>
  		<script type="text/javascript">
  			Cufon.replace('h1, #content h2, #content h3, #header .tagline');
		</script>
  	</head>

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-19015123-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

	<body>
		<div id="page-container">
			<div id="page">
				<div id="header">
					<h1 class="sitename"><span>OpenSHAPA</span></h1>
					<div class="tagline">
						Discovering more, faster<em> Exploratory Sequential Data Analysis</em>
					</div>
					<div class="clear"></div>
				</div>
				<!-- /header -->
				<div id="content-container">
					<div id="sidebar">
						<div id="navigation">
							<ul id="mainNav">
                                                                <li><a href="index.html">Introduction</a></li>
								<li><a href="http://www.openshapa.org/news">News</a></li>
								<li class="current"><a href="http://www.openshapa.org/snapshot.rb">Development Snapshots</a></li>
								<li><a href="https://github.com/OpenSHAPA/openshapa/wiki" target="_blank">Contributer Guide</a></li>
								<li><a href="https://openshapa.org/support" target="_blank">Support</a></li>
								<li><a href="https://www.pivotaltracker.com/projects/495691" target="_blank">Issue Database</a></li>
								<li><a href="https://github.com/OpenSHAPA" target="_blank">Source Code</a></li>
                                                                <li><a href="workshop.html">Workshop 2011</a></li>
                                                                <li><a href="https://openshapa.org/share"><b>Databrary</b></a></li>
							</ul>
						</div>
						<div id="sidebar-content">
							<h2>Major Supporters</h2>
							<ul class="logos">
								<li><a href="http://www.nicta.com.au" target="_blank"><img src="images/supporters/logo_NICTA.gif" alt="NICTA" /></a></li>
								<li><a href="http://www.uq.edu.au" taget="_blank"><img src="images/supporters/logo_uni_qld.gif" alt="The University of Queensland Australia" /></a></li>
								<li><a href="http://www.nyu.edu" target="_blank"><img src="images/supporters/logo_uni_ny.gif" alt="New York University" /></a></li>
							</ul>
							<h2><a href="supporters.html">Other Supporters</a></h2>
						</div>
					</div>
					<!-- /sidebar -->
					<div id="content">

<h2>Why is there a password?</h2>
At present we're deprecating a third-party dependency that is incompatible with
the Open Source license we are using with OpenSHAPA (GPLv3). The development snapshots
listed below are free of that dependency, and are freely accessible to everyone
without password! However, they are rough around the edges and we would love your
help testing and repairing them. Very soon the development snapshots will match
the functionality and stability of pre Open Source versions of OpenSHAPA.
<br/><br/>

<h2>Latest snapshot build for release #{snapshot_release[:version]}</h2>
This page contains the latest snapshot builds for OpenSHAPA, they contain the
very latest development work, and are a little unstable. However we need your
help to try and test the latest features.
<br/><br/>

<h3>Development Snapshot Download:</h3>
EOS

snapshot_downloads.each do |download|
  puts "<strong><a href=\"#{$DOWNLOAD_URL + download[:file]}\">#{download[:file]}</a></strong><br/>"
end

puts <<EOS
<br/><br/>
<h3>Devlopment Snapshot Status:</h3>
<table class="bodyTable"><tr class="a"><th>Fixes included in this build that require verification:</th></tr>
<tr>Nothing needs to be verified.</tr>
</table>
<br/><br/>
<table class="bodyTable"><tr class="a"><th>Fixes that have been verified to be included in this build:</th></tr>
EOS

if completed_stories.empty?
  print "<tr><td>No Completed fixes in this development release yet.</td></tr>"
end

odd = true
completed_stories.each do |story|
  if odd
    puts "<tr class=\"b\"><td><a href=\"#{story[:url]}\"> #{story[:name]} </a></td></tr>"
  else
    puts "<tr class=\"a\"><td><a href=\"#{story[:url]}\"> #{story[:name]} </a></td></tr>"
  end
end

puts <<EOS
</table>
<br/><br/><br/><br/><br/>
<h2>Previous snapshop builds:</h2>
<table class="bodyTable"><tr class="a"><th>Date</th>
<th>Build</th>
</tr>
EOS

odd = true
downloads.each do |download|
  if odd
    puts "<tr class=\"b\"><td>#{download[:date]}</td><td><a href=\"#{$DOWNLOAD_URL + download[:file]}\">#{download[:file]}</a></td></tr>"
  else
    puts "<tr class=\"a\"><td>#{download[:date]}</td><td><a href=\"#{$DOWNLOAD_URL + download[:file]}\">#{download[:file]}</a></td></tr>"
  end
end

puts <<EOS
</table>

					</div>
					<!-- /content -->
					<div class="clear"></div>
				</div>
				<!-- /content-container -->
				<div id="footer">
					Copyright &copy; 2009 OpenSHAPA Foundation
				</div>
				<!-- /footer -->
			</div>
			<!-- /page -->
		</div>
		<!-- /page-container -->


	</body>
</html>
EOS
