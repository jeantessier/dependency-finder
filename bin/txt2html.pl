#!/usr/bin/env perl -n

#   
#   Copyright (c) 2001-2025, Jean Tessier
#   All rights reserved.
#   
#   Redistribution and use in source and binary forms, with or without
#   modification, are permitted provided that the following conditions
#   are met:
#   
#       * Redistributions of source code must retain the above copyright
#         notice, this list of conditions and the following disclaimer.
#   
#       * Redistributions in binary form must reproduce the above copyright
#         notice, this list of conditions and the following disclaimer in the
#         documentation and/or other materials provided with the distribution.
#   
#       * Neither the name of Jean Tessier nor the names of his contributors
#         may be used to endorse or promote products derived from this software
#         without specific prior written permission.
#   
#   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
#   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
#   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
#   A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
#   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
#   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#   PROCU# ENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
#   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
#   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
#   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#   

BEGIN {
    $TEXT .= "<!doctype html>\n";
    $TEXT .= "\n";
    $TEXT .= "<html lang=\"en\">\n";
    $TEXT .= "\n";
    $TEXT .= "<head>\n";
    $TEXT .= "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />\n";
    $TEXT .= "<link rel=\"shortcut icon\" href=\"images/logoicon.gif\" type=\"image/gif\" />\n";
    $TEXT .= "<TITLE />\n";
    $TEXT .= "\n";
    $TEXT .= "<!-- Google tag (gtag.js) -->\n";
    $TEXT .= "<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-SHFGYZT6JY\"></script>\n";
    $TEXT .= "<script>\n";
    $TEXT .= "  window.dataLayer = window.dataLayer || [];\n";
    $TEXT .= "  function gtag(){dataLayer.push(arguments);}\n";
    $TEXT .= "  gtag('js', new Date());\n";
    $TEXT .= "\n";
    $TEXT .= "  gtag('config', 'G-SHFGYZT6JY');\n";
    $TEXT .= "</script>\n";
    $TEXT .= "\n";
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

    $TITLE = "<title>$title</title>\n" if $level == 1;

    $TEXT .= "<a name=\"$anchor\"></a>\n" if $level > 1;
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

s/=([^=]+)=/<code>\1<\/code>/g;
s/_([^_]+)_/<i>\1<\/i>/g;
s/\*([^*]+)\*/<b>\1<\/b>/g;
s/\[\[([^\]]*)\]\[(.*\.(gif|jpg|png|svg))\]\]/<a href="\1"><img src="\2" \/><\/a><br \/>/g;
s/\[\[([^\]]*\.(gif|jpg|png|svg))\]\]/<img src="\1" \/><br \/>/g;
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

    $TEXT =~ s/<TOC \/>/$TOC/g;

    $SHORT_TOC .= "</ul>\n";

    $TEXT =~ s/<SHORT-TOC \/>/$SHORT_TOC/g;

    $TEXT =~ s/<TITLE \/>/$TITLE/g;

    $VERSION = $ENV{"TXT2HTML_VERSION"};
    $NEXTVERSION = $VERSION . "-FUTURE";

    @VERSION = split(/\./, $VERSION);
    if (@VERSION > 1) {
        @VERSION[-1] += 1;
        $NEXTVERSION = join(".", @VERSION);
    }

    $TEXT =~ s/<VERSION \/>/$VERSION/g;
    $TEXT =~ s/<NEXTVERSION \/>/$NEXTVERSION/g;

    print $TEXT;
}
