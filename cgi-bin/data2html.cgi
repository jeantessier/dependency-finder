#!/usr/bin/perl

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

%MONTH = (
          "01" => "January",
          "02" => "February",
          "03" => "March",
          "04" => "April",
          "05" => "May",
          "06" => "June",
          "07" => "July",
          "08" => "August",
          "09" => "September",
          "10" => "October",
          "11" => "November",
          "12" => "December",
          );

$DIRNAME = "data";

if ($0 =~ /(\w+)\./) {
    $DOCUMENT = $1;
}

if (!grep { /--no-headers/ } @ARGV) {
    &PrintContentType();
}
&PrintDocumentHeader($DOCUMENT);
&PrintDocumentParts($DOCUMENT);
&PrintDocumentFooter();

sub PrintContentType {
    print "Content-type: text/html\n";
    print "\n";
}

sub PrintDocumentHeader {
    local ($document) = @_;

    open(FILEHANDLE, "$DIRNAME/${document}_title.txt");
    local ($title, @subtitle) = <FILEHANDLE>;
    chomp $title;
    close(FILEHANDLE);

    print "<!doctype html>\n";
    print "\n";
    print "<html lang=\"en\">\n";
    print "\n";
    print "<head>\n";
    if (grep { /--top-level/ } @ARGV) {
        print "<link rel=\"stylesheet\" type=\"text/css\" href=\"tufte.min.css\" />\n";
        print "<link rel=\"stylesheet\" type=\"text/css\" href=\"Journal.css\" />\n";
        print "<link rel=\"shortcut icon\" href=\"images/logoicon.gif\" type=\"image/gif\" />\n";
    } else {
        print "<link rel=\"stylesheet\" type=\"text/css\" href=\"../tufte.min.css\" />\n";
        print "<link rel=\"stylesheet\" type=\"text/css\" href=\"../Journal.css\" />\n";
        print "<link rel=\"shortcut icon\" href=\"../images/logoicon.gif\" type=\"image/gif\" />\n";
    }
    print "<title>$title</title>\n";
    print "\n";
    print "<!-- Google tag (gtag.js) -->\n";
    print "<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-SHFGYZT6JY\"></script>\n";
    print "<script>\n";
    print "  window.dataLayer = window.dataLayer || [];\n";
    print "  function gtag(){dataLayer.push(arguments);}\n";
    print "  gtag('js', new Date());\n";
    print "\n";
    print "  gtag('config', 'G-SHFGYZT6JY');\n";
    print "</script>\n";
    print "\n";
    print "</head>\n";
    print "\n";
    print "<body>\n";
    print "\n";
    print "<article>\n";
    print "<section>\n";
    print "<h1>$title</h1>\n";
    print "<p>\n";
    print @subtitle;
    print "</p>\n";
    print "</section>\n";
    print "\n";
}

sub PrintDocumentParts {
    local ($document) = @_;

    opendir(DIRHANDLE, $DIRNAME);
    local (@files) = grep { /^${document}_\d{4}-\d{2}-\d{2}.txt$/ } readdir(DIRHANDLE);
    closedir(DIRHANDLE);

    foreach $file (reverse sort @files) {
        &PrintDocumentPart("$DIRNAME/$file");
    }
}

sub PrintDocumentPart {
    local ($filename) = @_;

    local ($year, $month, $day);
    if ($filename =~ /(\d{4})-(\d{2})-(\d{2})/) {
        $year = $1;
        $month = $2;
        $day = $3;
    }

    print "<section>\n";
    print "<h2><a name=\"$year-$month-$day\">$MONTH{$month} $day, $year</a></h2>\n";

    local ($in_paragraph, $in_quote, $in_ordered_list, $in_unordered_list, $in_html);

    open(FILEHANDLE, $filename);

    local ($line);
    while ($line = <FILEHANDLE>) {
        if ($line =~ /^\s*$/) {
            if ($in_paragraph) {
                $in_paragraph = !$in_paragraph;
                print "</p>\n";
            } elsif ($in_quote) {
                $in_quote = !$in_quote;
                print "</pre>\n";
            } elsif ($in_ordered_list) {
                $in_ordered_list = !$in_ordered_list;
                print "</ol>\n";
            } elsif ($in_unordered_list) {
                $in_unordered_list = !$in_unordered_list;
                print "</ul>\n";
            } elsif ($in_html) {
                $in_html = !$in_html;
            }
        } elsif ($line =~ /^(\s*)((\S+)\s*(\S.*))/) {
            local ($indent, $text, $marker, $content) = ($1, $2, $3, $4);

            local ($indent_level) = length $indent;
            chomp $text;
            chomp $content;

            if (!$in_paragraph && !$in_quote && !$in_html && ($marker =~ /^\d+$/ || $marker eq "*")) {
                $line = "<li>$content</li>\n";
            }

            if (!$in_paragraph && !$in_quote && !$in_html && !$in_ordered_list && !$in_unordered_list && !$in_html) {
                if ($indent_level) {
                    if ($marker =~ /^\d+$/ && !$in_ordered_list) {
                        $in_ordered_list = !$in_ordered_list;
                        print "<ol>\n";
                    } elsif ($marker eq "*" && !$in_unordered_list) {
                        $in_unordered_list = !$in_unordered_list;
                        print "<ul>\n";
                    } elsif (!$in_quote) {
                        $in_quote = !$in_quote;
                        print "<pre>\n";
                    }
                } elsif ($line =~ /^</) {
                    $in_html = !$in_html;
                } else {
                    $in_paragraph = !$in_paragraph;
                    print "<p>\n";
                }
            }
        }

        $line =~ s/=([^=]+)=/<code>\1<\/code>/g;
        $line =~ s/_([^_]+)_/<i>\1<\/i>/g;
        $line =~ s/\*([^*]+)\*/<b>\1<\/b>/g;
        $line =~ s/\[\[([^\]]*)\]\[(.*\.(gif|jpg|png|svg))\]\]/<figure class=\"fullwidth\"><img src="\1" \/><\/figure>/gi;
        $line =~ s/\[\[([^\]]*\.(gif|jpg|png|svg))\]\]/<figure><img src="\1" \/><\/figure>/gi;
        $line =~ s/\[\[(\d\d\d\d-\d\d-\d\d)\]\]/<a href="#\1">\1<\/a>/gi;
        $line =~ s/\[\[([^\]]*)\]\[(.*)\]\]/<a target="_blank" href="\1">\2<\/a>/g;
        
        $line =~ s/%2A/\*/gi;
        $line =~ s/%3D/=/gi;
        $line =~ s/%5F/_/gi;

        if (grep { /--top-level/ } @ARGV) {
            $line =~ s/\.\.\///g;
        }

        print $line;
    }

    if ($in_paragraph) {
        print "</p>\n";
    } elsif ($in_quote) {
        print "</pre>\n";
    } elsif ($in_ordered_list) {
        print "</ol>\n";
    } elsif ($in_unordered_list) {
        print "</ul>\n";
    }

    close(FILEHANDLE);

    print "</section>\n";
    print "\n";
}

sub PrintDocumentFooter {
    print "\n";
    print "</article>\n";
    print "</body>\n";
    print "\n";
    print "</html>\n";
}
