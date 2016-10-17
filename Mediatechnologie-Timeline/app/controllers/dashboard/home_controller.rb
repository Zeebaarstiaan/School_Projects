class Dashboard::HomeController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  def dashboard
  end
end
