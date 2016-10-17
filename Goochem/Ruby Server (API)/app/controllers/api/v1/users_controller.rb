# file: app/controllers/api/v1/users_controller.rb
class Api::V1::UsersController < ApplicationController
  # before_filter :verify_is_user

  respond_to :json

  def index
    @users = User.all
    render :json => @users
  end

  def profile
    @user = current_user
    render :json => {:success => true, :user => @user}, status: 200
  end

  def update
    @user = User.find(current_user.id)

    if @user.nil?
      logger.info("User not found.")
      render :status => 404, :json => {:status => "error", :errorcode => "11009", :message => "Invalid userid."}
    else
      if @user.update_attributes(account_update_params)
        render :json => {:success => true, :info => t("devise.registrations.updated"), :user => @user }, status: 200
      else
        warden.custom_failure!
        render :json=> { :success => false, :info => user.errors}, :status=>422
      end
    end
  end

  def proximity_search
    distance = params[:data][:distance] unless params[:data].blank?
    @user = User.find(current_user.id)

    if distance && @user
      @nearby_users = User.close_to(@user,distance)
    else
      render :json => {:success => false, :info => "Something went wrong. Please try again!", :users => {}}
    end
  end


  private
  def verify_is_admin
    (current_user.nil?) ? redirect_to(root_path) : (redirect_to(root_path) unless current_user.admin?)
  end
  def account_update_params
    params.require(:user).permit(:name, :lonlat)
  end
end