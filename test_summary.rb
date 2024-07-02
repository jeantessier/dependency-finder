require 'nokogiri'

puts "| Subproject | Status | Tests | Skipped | Failures | Errors |"
puts "|------------|:------:|:-----:|:-------:|:--------:|:------:|"

Dir.glob('*/build/test-results/test/TEST-*.xml')
   .group_by {|name| name.split(%'/', 2).first}
   .each do |group, test_results|
      tests = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end
                 .map {|doc| doc.xpath "//@tests"}
                 .flatten
                 .map(&:value)
                 .map(&:to_i)
                 .sum

      skipped = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end
                          .map {|doc| doc.xpath "//@skipped"}
                          .flatten
                          .map(&:value)
                          .map(&:to_i)
                          .sum

      failures = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end
                            .map {|doc| doc.xpath "//@failures"}
                            .flatten
                            .map(&:value)
                            .map(&:to_i)
                            .sum

      errors = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end
                            .map {|doc| doc.xpath "//@errors"}
                            .flatten
                            .map(&:value)
                            .map(&:to_i)
                            .sum

      status = failures == 0 && errors == 0

      puts "| #{group} | #{status ? ":white_check_mark:" : ":x:"} | #{tests} | #{skipped} | #{failures} | #{errors} |"
    end

