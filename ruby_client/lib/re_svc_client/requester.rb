require 'rubygems'
require 'json'
require 'hashie/mash'
require 'digest/sha2'
require 'httpclient'

module ReSvcClient

  class Requester
    #AUTH_HEADER_NAME = 'Authorization'
    AUTH_HEADER_NAME = 'X-Nonce'

    def millisec_timestamp
      (Time.now.to_f * 1000).to_i
    end

    def generate_auth_header method, params, client, shared_secret, path, options={}
      timestamp = millisec_timestamp
      ottoken = generate_header_ottoken method, params, client, shared_secret, path, timestamp
      {AUTH_HEADER_NAME => "#{ottoken} #{client} #{timestamp}"}        
    end

    def generate_header_ottoken method, params, client, shared_secret, path, timestamp
      content = params.collect {|k,v| "#{k}=#{CGI.escape v.to_s}"}.join('&')
      Digest::SHA256.hexdigest("#{method}#{path}#{content}#{client}#{shared_secret}#{timestamp.to_s}")
    end
    
    def make_request method, params, client, shared_secret, server, action, controller=nil, options={}
      path = ""    
      if action.include?("/") && !controller 
        path = action
      else
        path << "/#{controller}"  if controller && !controller.empty?
        path << "/#{action}"      
      end
      options[:call_path] = path
      
      header = generate_auth_header method, params, client, shared_secret, path, options
      
      httpmethod = method.downcase.to_sym
      
      url = "#{server}#{path}"
      res = httpclient.send(httpmethod, url, params, header)
      @result = res            
      if res && res.body && !res.body.empty?
        @json_result = Hashie::Mash.new (JSON.parse(res.body))
      else
        @json_result = nil
      end
      
      res
    end
    
    def body
      @result.body
    end
    
    def code
      @result.code
    end
    
    def data 
      @json_result
    end
    
    def httpclient
      @httpclient ||= HTTPClient.new
    end
    
  end
  
  
end
