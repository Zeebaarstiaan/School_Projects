class UsersController < ApplicationController
  before_filter :signed_in_user, only: [:index, :edit, :update]
  before_filter :correct_user,   only: [:show, :edit, :update, :history]
  before_filter :admin_user,     only: [:destroy]

  def index
    @users = User.paginate(page: params[:page], :per_page => 10)
  end

  def show
    @user = User.find(params[:id])
  end

  def new
  	@user = User.new
  end

  def create
    @user = User.new
    @user.accessible = :all if current_user.account_type == 2
    @user.attributes = params[:user]
    if @user.save
      flash[:success] = "Gebruiker geregistreerd!"
      redirect_to @user
    else
      render 'new'
    end
  end

  def edit
    @user = User.find(params[:id])
  end

  def update
    @user = User.find(params[:id])
    @user.accessible = :all if current_user.account_type == 2
    if @user == current_user
      if @user.update_attributes(params[:user])
        flash[:success] = "Gebruiker bijgewerkt"
        sign_in @user
        redirect_to @user
      else
        render 'edit'
      end
    else
      if @user.update_attributes(params[:user])
        flash[:success] = "Gebruiker bijgewerkt"
        redirect_to @user
      else
        render 'edit'
      end
    end
  end

  def history
    @user = User.find(params[:id])
    @views = @user.views.paginate(:page => params[:page], :per_page => 5).order('created_at DESC')
  end

  def destroy
    User.find(params[:id]).destroy
    flash[:success] = "Gebruiker verwijderd"
    redirect_to users_path
  end

  private

    def signed_in_user
      unless signed_in?
        store_location
        redirect_to signin_path, notice: "U moet eerst inloggen."
      end
    end

    def correct_user
      @user = User.find(params[:id])
      redirect_to(root_path) unless current_user?(@user) || has_permissions?
    end

    def admin_user
      redirect_to(root_path) unless admin?
    end
end
