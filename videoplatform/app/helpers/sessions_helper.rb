module SessionsHelper
  def sign_in(user)
    cookies.permanent[:remember_token] = user.remember_token
    self.current_user = user
  end

  def signed_in?
    !current_user.nil?
  end

  def admin?
    current_user.account_type == 2
  end

  def teacher?
    current_user.account_type == 1
  end

  def has_permissions?
    current_user.account_type >= 1
  end

  def user_type
    case current_user.account_type
    when 0
      return "Leerling"
    when 1
      return "Docent"
    when 2
      return "Administrator"
  end


  end

  def current_user=(user)
    @current_user = user
  end

  def current_user
    @current_user ||= User.find_by_remember_token(cookies[:remember_token])
  end

  def current_user?(user)
    user == current_user
  end

  def sign_out
    self.current_user = nil
    cookies.delete(:remember_token)
  end

  def redirect_back_or(default)
    redirect_to(session[:return_to] || default)
    session.delete(:return_to)
  end

  def store_location
    session[:return_to] = request.fullpath
  end
end
