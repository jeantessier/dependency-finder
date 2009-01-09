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

require 5;

package TemplateProcessorRule;

sub new {
    my $type = shift;
    my $self = {};
    bless $self, $type;

    local ($target, @replacement) = @_;
    $self->Target ($target);
    $self->Replacement (@replacement);

    $self;
}

sub Target {
    my $self = shift;
    local ($value) = @_;

    $self->{'target'} = $value if @_;
        
    $self->{'target'};
}

sub Replacement {
    my $self = shift;
    local (@value) = @_;

    if (@_) {
        $self->{'replacement'} = \@value;
    } else {
        local ($array_ref) = $self->{'replacement'};
        @value = @$array_ref if ref $array_ref;
    }

    @value;
}

sub AddReplacements {
    my $self = shift;
    local (@replacements) = @_;
    local (@new_replacements);

    local ($array_ref) = $self->{'replacement'};
    @new_replacements = @$array_ref if ref $array_ref;

    push (@new_replacements, @replacements);

    $self->{'replacement'} = \@new_replacements;

    @new_replacements;
}

sub DeleteReplacements {
    my $self = shift;
    local ($replacement_count) = 0;

    local ($array_ref) = $self->{'replacement'};
    $replacement_count = @$array_ref if ref $array_ref;

    delete $self->{'replacement'};

    $replacement_count;
}

sub ToText {
    my $self = shift;
    local (@text);

    $text[++$#text] = (ref $self) . "\n";

    $text[++$#text] = "target\n";
    $text[++$#text] = "\t" . $self->Target . "\n";
    $text[++$#text] = "replacement\n";

    foreach $line ($self->Replacement) {
        $text[++$#text] = "\t" . $line . "\n";
    }

    @text;
}

1;
