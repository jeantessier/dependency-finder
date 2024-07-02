require 'nokogiri'

tests = Dir.glob('*/build/test-results/test/TEST-*.xml').map do |name|
  File.open(name) {|f| Nokogiri::XML f}
end
           .map {|doc| doc.xpath "//@tests"}
           .flatten
           .map(&:value)
           .map(&:to_i)
           .sum

puts "There were #{tests} test(s)."
