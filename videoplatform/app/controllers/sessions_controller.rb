class SessionsController < ApplicationController
  def new
  end

  def create
    user = User.find_by_login_code(params[:session][:login_code])
    if user && user.authenticate(params[:session][:password])
      user.update_attribute(:last_login, Time.new)
      sign_in user
      redirect_back_or user
   else
      flash.now[:error] = 'Inlog-code of wachtwoord klopt niet'
      render 'new'
    end
  end

  def destroy
    sign_out
    redirect_to root_path
  end
end
