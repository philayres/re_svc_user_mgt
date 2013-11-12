require 'net/http'
require 're_svc_client/requester'

module ReSvcClient
  describe "Token Test" do

    CLIENT_NAME = 'opclient1'
    SERVER = 'http://localhost:8000'
    WEBSITE_CLIENT = 1
    ADMIN_CLIENT = 999

    before(:each) do
      @requester = Requester.new
    end

    it "should attempt to create a new client" do

      shared_secret = 'test123!'
      method  = 'POST'    
      path = "/client_machines"       


      params = {username: 'opadmin', auth_type: ADMIN_CLIENT, password: 'test123!', client_name: CLIENT_NAME, client_type: WEBSITE_CLIENT}

      header = @requester.generate_auth_header method, params, params[:client_name], shared_secret, path

      uri = URI.parse("#{SERVER}#{path}")    
      req = Net::HTTP::Post.new(uri)
      req.set_form_data params
      header.each {|k,v| req.add_field k,v }


      res2 = Net::HTTP.start(uri.hostname, uri.port) do |http|
        http.request(req)
      end

      res2.body.should == '{"error":"Duplicate client name"}'

      res2 = Net::HTTP.start(uri.hostname, uri.port) do |http|
        http.request(req)
      end

      res2.body.should == '{"error":"Nonce check failed (Nonce has been used)"}'

    end
  end
end
