<?php
include 'config.php';

// Scan the directory of downloads and build a listing.
$dh = opendir($DOWNLOAD_DIR);
$files = array();
while ($file = readdir($dh)) {
    if ($file != "." && $file != "..") {
        // Parse the time of the download.
        $chunks = explode("-", $file);
        $date = explode(".", $chunks[3]);

        if (strlen($date[0]) == 10) {
            $date[0] = $date[0]."5959";
        }

        if (strlen($date[0]) == 8) {
            $date[0] = $date[0]."235959";
        }

        if ($files[$date[0]] == null) {
            $files[$date[0]] = array();
        }
        array_push($files[$date[0]], $file);
    }
}
closedir($dh);
ksort($files, SORT_NUMERIC);

// Log into FogBugz and get the token for the session.
$raw = file_get_contents($FOGBUGZ_URL."api.xml");
$raw = simplexml_load_string(file_get_contents($FOGBUGZ_URL."api.php?cmd=logon&email=".$FOGBUGZ_USER."&password=".$FOGBUGZ_PASS));
$token = $raw->token;

// List all the miletones for the project.
$raw = simplexml_load_string(file_get_contents($FOGBUGZ_URL."api.php?cmd=listFixFors&token=".$token."&ixProject=3"));

$releases = array();
foreach ($raw->fixfors->fixfor as $value) {
    if ($value->sFixFor != "Undecided") {
        echo "\n";
        $release_date = str_replace("-", "", $value->dt);
        $release_date = substr($release_date, 0, strpos($release_date, "T"))."235959";
        $releases[$release_date] = $value->sFixFor;
   }
}

// Get the last build date and corresponding file.
$last_build_date = array_pop(array_keys($files));
$last_build_file = array_pop(array_values($files));
array_pop($files);

// Sort the remaining builds.
krsort($files, SORT_NUMERIC);

// Sort the milestones by date.
asort($releases, SORT_NUMERIC);

// Search for the release that matches the current build
foreach ($releases as $key => $value) {
    if ($last_build_date < $key) {
        $matching_release = $value;
        $release_date = $key;
        break;
    }
}

// Search for the bugs assigned to the release that matches the latest build.
$raw = 
simplexml_load_string(file_get_contents($FOGBUGZ_URL."api.php?cmd=search&token=".$token."&cols=ixBug,sTitle,sStatus,sPersonAssignedTo,dtResolved&q=release:".$matching_release));

// Sort the bugs based on status.
$fixed_bugs = array();
$closed_bugs = array();
$open_bugs = array();
foreach ($raw->cases->case as $key => $value) {    
    $title = (string) $value->sTitle;

    if (strlen($title) > $SUMMARY_LENGTH) {
        $title = substr($title, 0, $SUMMARY_LENGTH) . "...";
    }
    $person = (string) $value->sPersonAssignedTo;

    if ($value->sStatus == "Active") {
        $open_bugs[(int) $value->ixBug] = array($title, $person);
    } else if (($value->sStatus == "Resolved (Fixed)"
                || $value->sStatus == "Resolved (By Design)"
                || $value->sStatus == "Resolved (Not Reproducible)"
                || $value->sStatus == "Resolved (Implemented)")
               && (strtotime($last_build_date) > strtotime($value->dtResolved))  ) {
        $fixed_bugs[(int) $value->ixBug] = array($title, $person);
    } else if ($value->sStatus == "Closed (Fixed)"
               || $value->sStatus == "Closed (Implemented)"
               || $value->sStatus == "Closed (Not Reproducible)"
               || $value->sStatus == "Closed (By Design)") {
        $closed_bugs[(int) $value->ixBug] = array($title, $person);
    }
}

// logout of the FogBugz API.
$raw = file_get_contents($FOGBUGZ_URL."api.php?cmd=logoff&token=".$token);
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<title>OpenSHAPA - Making Research Easier</title>
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
						Making Research Easier<em> Exploratory Sequential Data Analysis</em>
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
								<li class="current"><a href="http://www.openshapa.org/dev/snapshot.php">Development Snapshots</a></li>
                                                                <li><a href="publications.html">Publications</a></li>
								<li><a href="https://openshapa.org/fogbugz/default.php?W19" target="_blank">User Guide</a></li>
								<li><a href="https://openshapa.org/fogbugz/default.php?W20" target="_blank">Contributer Guide</a></li>
								<li><a href="https://openshapa.org/fogbugz/default.php?OpenSHAPA" target="_blank">Forum</a></li>
								<li><a href="https://openshapa.org/fogbugz/" target="_blank">Issue Database</a></li>
								<li><a href="http://www.openshapa.org/dev/clover" target="_blank">Code Coverage</a></li>
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

<h2>Latest snapshot build for release - <?= $matching_release ?></h2>
<h3>Due <?php print date("jS F o", strtotime($release_date)); ?>:</h3>
This page contains the latest snapshot builds for OpenSHAPA, they contain the
very latest development work, and are a little unstable. However we need your
help to try and test the latest features.

<br/><br/>
<h3>Development Snapshot Download:</h3>
<?php
foreach($last_build_file as $file) {
    print "<strong><a href=\"".$DOWNLOAD_URL.$file."\">".$file."</a></strong><br/>";
}
?>


<br/><br/>
<h3>Devlopment Snapshot Status:</h3>
<table class="bodyTable"><tr class="a"><th>Fixes included in this build that require verfication:</th>
</tr>
<?php
$odd = true;

foreach ($fixed_bugs as $key => $value) {
    if ($odd) {
        print "<tr class=\"b\"><td>" . $value[1] . " still needs to verify that <a href=\"" . $FOGBUGZ_URL . "default.php?pgx=EV&ixBug=" . $key . "\">Case "
. $key . ":" . $value[0] . "</a> has been fixed in this build.<br></td></tr>";
    } else {
        print "<tr class=\"a\"><td>" . $value[1] . " still needs to verify that <a href=\"" . $FOGBUGZ_URL . "default.php?pgx=EV&ixBug=" . $key . "\">Case "
. $key . ":" . $value[0] . "</a> has been fixed in this build.<br></td></tr>";
    }

    $odd = !$odd;
}
?></table>

<br/><br/>
<table class="bodyTable"><tr class="a"><th>Fixes that have been verified to be included in this build:</th>
</tr>
<?php
$odd = true;

foreach ($closed_bugs as $key => $value) {
    if ($odd) {
        print "<tr class=\"b\"><td><a href=\"" . $FOGBUGZ_URL . "default.php?pgx=EV&ixBug=" . $key . "\">Case " . $key . ":" . $value[0] . "</a></td></tr>";
    } else {
        print "<tr class=\"a\"><td><a href=\"" . $FOGBUGZ_URL . "default.php?pgx=EV&ixBug=" . $key . "\">Case " . $key . ":" . $value[0] . "</a></td></tr>";
    }

    $odd = !$odd;
}
?></table>


<br/><br/><br/><br/><br/>
<h2>Previous snapshop builds:</h2>
<table class="bodyTable"><tr class="a"><th>Date</th>
<th>Build</th>
</tr>

<?php
$odd = true;
foreach ($files as $key => $value) {
    if ($odd) {
        foreach($value as $file) {
            print "<tr class=\"b\"><td>" . date("jS F o - g:ia", strtotime($key)) . "</td><td><a href=\"".$DOWNLOAD_URL.$file."\">". $file . "</a></td></tr>";
        }
    } else {
        foreach($value as $file) {
            print "<tr class=\"a\"><td>" . date("jS F o - g:ia", strtotime($key)) . "</td><td><a href=\"".$DOWNLOAD_URL.$file."\">". $file . "</a></td></tr>";
        }
    }
    $odd = !$odd;
}
?>
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

