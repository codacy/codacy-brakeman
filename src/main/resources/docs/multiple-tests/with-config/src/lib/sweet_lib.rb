##Patterns: Execute
class SweetLib
  def do_some_cool_stuff bad
      ##Warn: Execute
    `ls #{bad}`
  end

  def test_command_injection_in_lib
    #Should warn about command injection
        ##Warn: Execute
    system("rm #{@bad}")
  end

  def test_net_http_start_ssl
    Net::HTTP.start(uri.host, uri.port, :use_ssl => true, :verify_mode => OpenSSL::SSL::VERIFY_NONE)
  end
end
