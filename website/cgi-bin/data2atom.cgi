#!/usr/bin/perl

#   
#   Copyright (c) 2001-2009, Jean Tessier
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

&PrintContentType();
&PrintDocumentHeader($DOCUMENT);
&PrintDocumentParts($DOCUMENT);
&PrintDocumentFooter();

sub PrintContentType {
    print "Content-type: application/atom+xml\n";
    print "\n";
}

sub PrintDocumentHeader {
    local ($document) = @_;

    open(FILEHANDLE, "$DIRNAME/${document}_title.txt");
    local ($title, @subtitle) = <FILEHANDLE>;
    chomp $title;
    close(FILEHANDLE);

    print "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    print "\n";
    print "<feed xmlns=\"http://www.w3.org/2005/Atom\">\n";
    print "\n";
    print "    <title>Dependency Finder - $title</title>\n";
    print "    <id>http://depfind.sf.net/cgi-bin/$DOCUMENT.cgi</id>\n";
    print "    <link href=\"http://depfind.sf.net/cgi-bin/$DOCUMENT.cgi\"/>\n";
    print "    <author>\n";
    print "        <name>Jean Tessier</name>\n";
    print "    </author>\n";
    print "    <rights type=\"xhtml\">Copyright (c) 2001-2009, Jean Tessier</rights>\n";
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

    print "\n";
    print "    <entry>\n";
    print "        <title>$MONTH{$month} $day, $year</title>\n";
    print "        <id>http://depfind.sf.net/cgi-bin/Journal.cgi#$year-$month-$day</id>\n";
    print "        <link href=\"http://depfind.sf.net/cgi-bin/Journal.cgi#$year-$month-$day\"/>\n";
    print "        <published>$year-$month-$day</published>\n";
    print "        <content type=\"xhtml\">\n";

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

        $line =~ s/=([^=]*)=/<code>\1<\/code>/g;
        $line =~ s/_([^_]*)_/<i>\1<\/i>/g;
        $line =~ s/\*([^*]*)\*/<b>\1<\/b>/g;
        $line =~ s/\[\[(http[^\]]*)\]\[(http.*\.((gif)|(jpg)|(png)))\]\]/<a target="_blank" href="\1"><img border="0" src="\2" \/><\/a><br \/>/gi;
        $line =~ s/\[\[(http[^\]]*)\]\[(.*\.((gif)|(jpg)|(png)))\]\]/<a target="_blank" href="\1"><img border="0" src="http:\/\/depfind.sf.net\/cgi-bin\/\2" \/><\/a><br \/>/gi;
        $line =~ s/\[\[([^\]]*)\]\[(http.*\.((gif)|(jpg)|(png)))\]\]/<a target="_blank" href="http:\/\/depfind.sf.net\/cgi-bin\/\1"><img border="0" src="\2" \/><\/a><br \/>/gi;
        $line =~ s/\[\[([^\]]*)\]\[(.*\.((gif)|(jpg)|(png)))\]\]/<a target="_blank" href="http:\/\/depfind.sf.net\/cgi-bin\/\1"><img border="0" src="http:\/\/depfind.sf.net\/cgi-bin\/\2" \/><\/a><br \/>/gi;
        $line =~ s/\[\[(http[^\]]*\.((gif)|(jpg)|(png)))\]\]/<img src="\1" \/><br \/>/gi;
        $line =~ s/\[\[([^\]]*\.((gif)|(jpg)|(png)))\]\]/<img src="http:\/\/depfind.sf.net\/cgi-bin\/\1" \/><br \/>/gi;
        $line =~ s/\[\[(\d\d\d\d-\d\d-\d\d)\]\]/<a href="http:\/\/depfind.sf.net\/cgi-bin\/Journal.cgi#\1">\1<\/a>/gi;
        $line =~ s/\[\[(http[^\]]*)\]\[(.*)\]\]/<a target="_blank" href="\1">\2<\/a>/g;
        $line =~ s/\[\[([^\]]*)\]\[(.*)\]\]/<a target="_blank" href="http:\/\/depfind.sf.net\/cgi-bin\/\1">\2<\/a>/g;

        $line =~ s/%2A/\*/gi;
        $line =~ s/%3D/=/gi;
        $line =~ s/%5F/_/gi;

        $line =~ s/&(?!amp|lt|gt)/&amp;/g;
        $line =~ s/<-/&lt;-/g;
        $line =~ s/->/-&gt;/g;

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

    print "        </content>\n";
    print "    </entry>\n";
}

sub PrintDocumentFooter {
    print "\n";
    print "</feed>\n";
}
