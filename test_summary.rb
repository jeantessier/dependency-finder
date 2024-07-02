require 'nokogiri'

puts "| Subproject | Status | Tests | Passed | Skipped | Failures | Errors |"
puts "|------------|:------:|:-----:|:------:|:-------:|:--------:|:------:|"

Dir.glob('*/build/test-results/test/TEST-*.xml')
   .group_by {|name| name.split(%'/', 2).first}
   .each do |group, test_results|
      docs = test_results.map do |name|
        File.open(name) {|f| Nokogiri::XML f}
      end

      counts = {
        tests: docs.map {|doc| doc.xpath("count(//testcase)")}.sum,
        passed: docs.map {|doc| doc.xpath("count(//testcase[not(*)])")}.sum,
        skipped: docs.map {|doc| doc.xpath("count(//testcase[skipped])")}.sum,
        failures: docs.map {|doc| doc.xpath("count(//testcase[failure])")}.sum,
        errors: docs.map {|doc| doc.xpath("count(//testcase[error])")}.sum,
      }

      status = counts[:failures] == 0 && counts[:errors] == 0

      puts "| #{group} | #{status ? ":white_check_mark:" : ":x:"} | #{counts[:tests]} | #{counts[:passed]} | #{counts[:skipped]} | #{counts[:failures]} | #{counts[:errors]} |"
    end

