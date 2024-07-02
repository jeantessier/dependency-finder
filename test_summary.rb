require 'nokogiri'

puts "| Subproject | Status | Tests | Skipped | Failures | Errors |"
puts "|------------|:------:|:-----:|:-------:|:--------:|:------:|"

Dir.glob('*/build/test-results/test/TEST-*.xml')
   .group_by {|name| name.split(%'/', 2).first}
   .each do |group, test_results|
      docs = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end

      counts = Hash.new(0)

      [
        :tests,
        :skipped,
        :failures,
        :errors,
      ].each do |attr|
            counts[attr] = docs.map {|doc| doc.xpath "//@#{attr}"}.flatten.map(&:value).map(&:to_i).sum
      end

      status = counts[:failures] == 0 && counts[:errors] == 0

      puts "| #{group} | #{status ? ":white_check_mark:" : ":x:"} | #{counts[:tests]} | #{counts[:skipped]} | #{counts[:failures]} | #{counts[:errors]} |"
    end

