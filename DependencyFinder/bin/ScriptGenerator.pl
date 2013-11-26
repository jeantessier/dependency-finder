#!/usr/local/bin/perl

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

use TemplateProcessor;

use Getopt::Std;

getopts(":cguvw");

%TEMPLATES = (
              "ClassClassDiff" => ".cli.template.txt",
              "ClassCohesion" => ".cli.template.txt",
              "ClassDump" => ".cli.template.txt",
              "ClassFinder" => ".cli.template.txt",
              "ClassList" => ".cli.template.txt",
              "ClassMetrics" => ".cli.template.txt",
              "ClassReader" => ".cli.template.txt",
              "ClosureToText" => ".xsl.template.txt",
              "DependablesToHTML" => ".xsl.template.txt",
              "DependablesToText" => ".xsl.template.txt",
              "DependencyClosure" => ".cli.template.txt",
              "DependencyCycles" => ".cli.template.txt",
              "DependencyExtractor" => ".cli.template.txt",
              "DependencyGraphToFullyQualifiedNames" => ".xsl.template.txt",
              "DependencyGraphToGraphML" => ".xsl.template.txt",
              "DependencyGraphToHTML" => ".xsl.template.txt",
              "DependencyGraphToRDF" => ".xsl.template.txt",
              "DependencyGraphToText" => ".xsl.template.txt",
              "DependencyGraphToUnconfirmedFullyQualifiedNames" => ".xsl.template.txt",
              "DependencyGraphToyEd" => ".xsl.template.txt",
              "DependencyMetrics" => ".cli.template.txt",
              "DependencyReporter" => ".cli.template.txt",
              "DependentsToHTML" => ".xsl.template.txt",
              "DependentsToText" => ".xsl.template.txt",
              "DiffToFullyQualifiedNames" => ".xsl.template.txt",
              "DiffToHTML" => ".xsl.template.txt",
              "HideInboundDependenciesToHTML" => ".xsl.template.txt",
              "HideInboundDependenciesToText" => ".xsl.template.txt",
              "HideOutboundDependenciesToHTML" => ".xsl.template.txt",
              "HideOutboundDependenciesToText" => ".xsl.template.txt",
              "JarJarDiff" => ".cli.template.txt",
              "ListDeprecatedElements" => ".cli.template.txt",
              "ListDiff" => ".cli.template.txt",
              "ListDiffToHTML" => ".xsl.template.txt",
              "ListDiffToText" => ".xsl.template.txt",
              "ListInheritanceDiffToText" => ".xsl.template.txt",
              "ListSymbols" => ".cli.template.txt",
              "ListUnused" => ".xsl.template.txt",
              "MetricsToHTML" => ".xsl.template.txt",
              "MetricsToText" => ".xsl.template.txt",
              "XSLTProcess" => ".cli.template.txt",
              "c2c" => ".reporter.template.txt",
              "c2p" => ".reporter.template.txt",
              "f2f" => ".reporter.template.txt",
              "p2p" => ".reporter.template.txt",
);

if ($opt_g) {
    &Generate("sh", "");
    &Generate("bat", ".bat");
} elsif ($opt_c) {
    &Clean("");
    &Clean(".bat");
} else {
    print STDERR "Please choose either generate (-g) or clean (-c) scripts\n";
}

sub Generate {
    local ($target_shell, $target_extension) = @_;

    foreach $command (sort keys %TEMPLATES) {
        &Process($command,
                 $target_shell . $TEMPLATES{$command},
                 $command . $target_extension);
    }
}

sub Clean {
    local ($target_extension) = @_;

    foreach $command (sort keys %TEMPLATES) {
        local ($filename) = $command . $target_extension;

        print "Deleting $filename\n" if $opt_v && -e $filename;
        unlink($filename) ;
    }
}

sub Process {
    local ($command, $template, $filename) = @_;
    
    local ($template_mtime) = (stat $template)[9];
    local ($filename_mtime) = (stat $filename)[9];

    if ($filename_mtime <= $template_mtime) {
        print "Generating $filename\n" if $opt_v;

        local ($processor) = new TemplateProcessor ($template);
        $processor->AddRules(new TemplateProcessorRule("##COMMAND##", $command));

        open (FILEHANDLE, "> $filename");
        print FILEHANDLE $processor->Process;
        close (FILEHANDLE);
    }
}
