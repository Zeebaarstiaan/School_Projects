# file: app/controllers/api/v1/tasks_controller.rb
class Api::V1::TasksController < ApplicationController
  # skip_before_filter :authenticate_user!, :only => :index



  # Just skip the authentication for now
  # before_filter :authenticate_user!

  respond_to :json

  def index
    render :text => '{
  "success":true,
  "info":"ok",
  "data":{
          "tasks":[
                    {"title":"Complete the app"},
                    {"title":"Complete the tutorial"}
                  ]
         }
}'
  end
end