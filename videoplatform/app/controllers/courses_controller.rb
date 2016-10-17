class CoursesController < ApplicationController
  before_filter :admin_user, only: [:new, :edit, :create, :update, :destroy]
  before_filter :signed_in

  def index
    @courses = Course.paginate(page: params[:page], :per_page => 10)
  end

  def new
    @course = Course.new
  end

  def show
      @course = Course.find(params[:id])
      @videos = @course.videos.paginate(:page => params[:page], :per_page => 5).order('created_at DESC')
  end

  def create
    @course = Course.new
    @course.accessible = :all if admin?
    @course.attributes = params[:course]
    if @course.save
      flash[:success] = "Vak aangemaakt!"
      redirect_to courses_path
    else
      render 'new'
    end
  end

  def edit
    @course = Course.find(params[:id])
  end

  def update
    @course = Course.find(params[:id])
    @course.accessible = :all if admin?
    if @course.update_attributes(params[:course])
      flash[:success] = "Vak bijgewerkt"
      redirect_to courses_path
    else
      render 'edit'
    end
  end

  def destroy
    Course.find(params[:id]).destroy
    flash[:success] = "Vak verwijderd"
    redirect_to courses_path
  end

  private

      def admin_user
        redirect_to(root_path) unless admin?
      end

      def signed_in
        redirect_to(root_path) unless signed_in?
      end
end
