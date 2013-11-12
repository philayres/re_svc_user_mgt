require 're_svc_client/requester'

module ReSvcClient

  describe Requester do

    CLIENT_NAME = 'opclient1'
    SERVER = 'http://localhost:8000'
    WEBSITE_CLIENT = 1
    EXTLOGIN = 2
    ADMIN_CLIENT = 999

    before(:each) do
      @requester = Requester.new
    end

    it "should make new client requests (duplicate clients)" do
      shared_secret = 'test123!'
      method  = 'POST'    
      path = "/client_machines"       

      new_client_name = CLIENT_NAME  

      params = {username: 'opadmin', auth_type: ADMIN_CLIENT, password: 'test123!', client_name: new_client_name, client_type: WEBSITE_CLIENT}


      @requester.make_request method, params, CLIENT_NAME, shared_secret, SERVER, path

      @requester.body.should == '{"error":"Duplicate client name"}'

      @requester.make_request method, params, CLIENT_NAME, shared_secret, SERVER, path    
      @requester.body.should == '{"error":"Duplicate client name"}'

    end

    it "should make new client requests (OK)" do
      shared_secret = 'test123!'
      method  = 'POST'    
      path = "/client_machines"       

      $new_client_name = "#{CLIENT_NAME}_#{rand(100000000).to_s}"
      params = {username: 'opadmin', auth_type: ADMIN_CLIENT, password: 'test123!', client_name: $new_client_name, client_type: WEBSITE_CLIENT}


      @requester.make_request method, params, CLIENT_NAME, shared_secret, SERVER, path

      @requester.data.client_id.should be_a Integer

      $new_client_id = @requester.data.client_id
      $new_secret = @requester.data.shared_secret
    end    

    it "should authenticate user" do

      method  = 'POST'    
      path = "/credentials/authenticate"       

      params = {username: 'opadmin', auth_type: ADMIN_CLIENT, password: 'test123!'}

      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path

      @requester.code.should == 200
      @requester.data.user_id.should == 1

    end

    it "should create new user"  do
      method = 'POST'
      path = '/users'
      $newuser = "newuser_#{rand(1000000)}"
      $newpassword = "pw!$#{rand(1000000)}"

      params = {username: $newuser, auth_type: WEBSITE_CLIENT, password: $newpassword, validated: true}
      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path
      @requester.code.should == 200
      @requester.data.user_id.should > 1

      $new_user_id = @requester.data.user_id
    end

    it "should authenticate new user" do

      method  = 'POST'    
      path = "/credentials/authenticate"       

      params = {username: $newuser, auth_type: WEBSITE_CLIENT, password: $newpassword}

      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path

      @requester.code.should == 200
      @requester.data.user_id.should == $new_user_id

    end    

    it "should add new credentials for user"  do
      $newuser2 = "newuser_#{rand(1000000)}"
      $newpassword2 = "pw!$#{rand(1000000)}"
      method  = 'POST'    
      path = "/credentials"       

      params = {new_username: $newuser2, new_password: $newpassword2, new_auth_type: EXTLOGIN, username: $newuser, password: $newpassword, auth_type: WEBSITE_CLIENT}

      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path

      @requester.code.should == 200
      @requester.data.should be_nil

    end

    it "should authenticate user with both sets of credentials" do

      method  = 'POST'    
      path = "/credentials/authenticate"       

      params = {username: $newuser, auth_type: WEBSITE_CLIENT, password: $newpassword}

      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path

      @requester.code.should == 200
      @requester.data.user_id.should == $new_user_id

      params = {username: $newuser2, auth_type: EXTLOGIN, password: $newpassword2}

      @requester.make_request method, params, $new_client_name, $new_secret, SERVER, path

      @requester.code.should == 200
      @requester.data.user_id.should == $new_user_id

    end    

  end

end