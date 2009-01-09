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

use TemplateProcessorRule;

package TemplateProcessor;

sub new {
    my $type = shift;
    my $self = {};
    bless $self, $type;

    local ($template, @rules) = @_;
    $self->Template ($template);
    $self->Rules (@rules);

    $self;
}

sub Template {
    my $self = shift;
    local ($value) = @_;

    $self->{'template'} = $value if @_;
        
    $self->{'template'};
}

sub Rules {
    my $self = shift;
    local (@value) = @_;

    if (@_) {
        $self->{'rules'} = \@value;
    } else {
        local ($array_ref) = $self->{'rules'};
        @value = @$array_ref if ref $array_ref;
    }

    @value;
}

sub AddRules {
    my $self = shift;
    local (@rules) = @_;
    local (@new_rules);

    local ($array_ref) = $self->{'rules'};
    @new_rules = @$array_ref if ref $array_ref;

    push (@new_rules, @rules);

    $self->{'rules'} = \@new_rules;

    @new_rules;
}

sub DeleteRules {
    my $self = shift;
    local ($rules_count) = 0;

    local ($array_ref) = $self->{'rules'};
    $rules_count = @$array_ref if ref $array_ref;

    delete $self->{'rules'};

    $rules_count;
}

sub Process {
    my $self = shift;

    local (@rules);
    if (@_) {
        @rules = @_;
    } else {
        @rules = $self->Rules;
    }

    open (FILEHANDLE, $self->{'template'});
    local ($text) = join ('', <FILEHANDLE>);
    close ($TEMPLATE);

    foreach $rule (@rules) {
        if (ref $rule) {
            local ($target) = $rule->Target;
            local ($replacement) = join ('', $rule->Replacement);

            $text =~ s/$target/$replacement/g;
        }
     }

     $text;
}

sub ToText {
    my $self = shift;
    local (@text);

    $text[++$#text] = (ref $self) . "\n";

    $text[++$#text] = "template\n";
    $text[++$#text] = "\t" . $self->Template . "\n";
    $text[++$#text] = "rules\n";

    foreach $rule ($self->Rules) {
        local (@subtext) = $rule->ToText;

        foreach $line (@subtext) {
            $text[++$#text] = "\t" . $line;
        }
    }

    @text;
}

1;
