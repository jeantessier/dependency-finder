#!/usr/local/bin/perl -n

BEGIN {
    $TEXT .= "<!doctype html PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n";
    $TEXT .= "\n";
    $TEXT .= "<html>\n";
    $TEXT .= "\n";
    $TEXT .= "<head>\n";
    $TEXT .= "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />\n";
    $TEXT .= "<TITLE />\n";
    $TEXT .= "</head>\n";
    $TEXT .= "\n";
    $TEXT .= "<body>\n";
    $TEXT .= "\n";

    $TOC_LEVEL = 1;
    $TOC = "<h2>Table of Contents</h2>\n";

    $SHORT_TOC = "<ul>\n";
}

if (/^---(\++)\s*(.*)\s*/) {
    $level = length $1;
    $title = $2;
    $anchor = $title;
    $anchor =~ s/\s//g;
    $anchor =~ s/=//g;

    while (exists $ANCHORS{$anchor}) {
	$anchor .= "X";
    }
    $ANCHORS{$anchor} = 1;

    while ($TOC_LEVEL < $level) {
	$TOC .= "<ul>\n";
	$TOC_LEVEL++;
    }
    while ($TOC_LEVEL > $level) {
	$TOC .= "</ul>\n";
	$TOC_LEVEL--;
    }
    $TOC .= "<li><a href=\"#$anchor\">$title</a></li>\n" if $TOC_LEVEL > 1;

    $SHORT_TOC .= "<li><a href=\"#$anchor\">$title</a></li>\n" if $TOC_LEVEL == 2;

    $TITLE = "<title>$title</title\n" if $level == 1;

    $TEXT .= "<a name=\"$anchor\" />\n" if $level > 1;
    $_ = "<h$level>$title</h$level>\n";
} elsif (/^-{4,}\s*$/) {
    $_ = "<hr />\n";
} elsif (/^\s*$/) {
    if ($in_paragraph) {
	$in_paragraph = !$in_paragraph;
	$TEXT .= "</p>\n";
    } elsif ($in_quote) {
	$in_quote = !$in_quote;
	$TEXT .= "</pre>\n";
    } elsif ($in_ordered_list) {
	$in_ordered_list = !$in_ordered_list;
	$TEXT .= "</ol>\n";
    } elsif ($in_unordered_list) {
	$in_unordered_list = !$in_unordered_list;
	$TEXT .= "</ul>\n";
    } elsif ($in_html) {
	$in_html = !$in_html;
    }
} elsif (/^(\s*)((\S+)\s*(\S.*))/) {
    local ($indent, $text, $marker, $content) = ($1, $2, $3, $4);

    local ($indent_level) = length $indent;
    chomp $text;
    chomp $content;

    if (!$in_paragraph && !$in_quote && !$in_html && ($marker =~ /^\d+$/ || $marker eq "*")) {
	$_ = "<li>$content</li>\n";
    }

    if (!$in_paragraph && !$in_quote && !$in_html && !$in_ordered_list && !$in_unordered_list && !$in_html) {
	if ($indent_level) {
	    if ($marker =~ /^\d+$/ && !$in_ordered_list) {
		$in_ordered_list = !$in_ordered_list;
		$TEXT .= "<ol>\n";
	    } elsif ($marker eq "*" && !$in_unordered_list) {
		$in_unordered_list = !$in_unordered_list;
		$TEXT .= "<ul>\n";
	    } elsif (!$in_quote) {
		$in_quote = !$in_quote;
		$TEXT .= "<pre>\n";
	    }
	} elsif (/^</) {
	    $in_html = !$in_html;
	} else {
	    $in_paragraph = !$in_paragraph;
	    $TEXT .= "<p>\n";
	}
    }
}

s/=([^=]*)=/<code>\1<\/code>/g;
s/_([^_]*)_/<i>\1<\/i>/g;
s/\*([^*]*)\*/<b>\1<\/b>/g;
s/\[\[([^\]]*)\]\[(.*\.jpg)\]\]/<a href="\1"><img src="\2" \/><\/a><br \/>/g;
s/\[\[([^\]]*\.gif)\]\]/<img src="\1" \/><br \/>/g;
s/\[\[([^\]]*\.jpg)\]\]/<img src="\1" \/><br \/>/g;
s/\[\[([^\]]*)\]\[(.*)\]\]/<a href="\1">\2<\/a>/g;

s/%2A/\*/gi;
s/%3D/=/gi;
s/%5F/_/gi;

$TEXT .= $_;

END {
    $TEXT .= "\n";
    $TEXT .= "</body>\n";
    $TEXT .= "\n";
    $TEXT .= "</html>\n";

    while ($TOC_LEVEL > 1) {
	$TOC .= "</ul>\n";
	$TOC_LEVEL--;
    }
    
    $TOC =~ s/=\b([^=]*)\b=/<code>\1<\/code>/g;

    $TEXT =~ s/<TOC \/>/$TOC/;

    $SHORT_TOC .= "</ul>\n";

    $TEXT =~ s/<SHORT-TOC \/>/$SHORT_TOC/;

    $TEXT =~ s/<TITLE \/>/$TITLE/;

    print $TEXT;
}
