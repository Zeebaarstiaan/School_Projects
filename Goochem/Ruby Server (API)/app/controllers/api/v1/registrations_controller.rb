class Api::V1::RegistrationsController < Devise::RegistrationsController
  before_filter :authenticate_user_from_token!, :only => :create
  respond_to :json

  # def create
  #   build_resource
  #   resource.skip_confirmation!
  #   if resource.save
  #     sign_in resource
  #     render :status => 200,
  #          :json => { :success => true,
  #                     :info => "Registered",
  #                     :data => { :user => resource,
  #                                :auth_token => current_user.authentication_token } }
  #   else
  #     render :status => :unprocessable_entity,
  #            :json => { :success => false,
  #                       :info => resource.errors,
  #                       :data => {} }
  #   end
  # end

  def create
    user = User.new(sign_up_params)
    if user.save
      render :json=> { :success => true,
                      :info => t("devise.registrations.signed_up"),
                      :user => { :username => user.name, :email => user.email,
                                 :auth_token => user.authentication_token,
                                 :lonlat => user.lonlat }}, :status=>201
      return
    else
      warden.custom_failure!
      render :json=> { :success => false,
                      :info => user.errors}, :status=>422
    end
  end

  private
  def sign_up_params
    params.require(:user).permit(:name, :email, :password, :password_confirmation, :lonlat)
  end
end