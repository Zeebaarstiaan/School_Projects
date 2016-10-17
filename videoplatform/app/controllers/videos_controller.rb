class VideosController < ApplicationController
  before_filter :signed_in_user, only: [:index, :show]
  before_filter :has_rights,   only: [:new, :edit, :update, :destroy]

  def index
    @videos = Video.paginate(page: params[:page], :per_page => 5)
  end

  def new
    @video = Video.new
    @courses = Course.all
  end

  def create
    @video = Video.new
    @video.accessible = :all if admin?
    @video.attributes = params[:video]
    if @video.save
      flash[:success] = "Video aangemaakt!"
      redirect_to @video
    else
      @courses = Course.all
      render 'new'
    end
  end

  def show
    @video = Video.find(params[:id])
    save_view(Video.find(params[:id]))
  end

  def edit
    @video = Video.find(params[:id])
    @courses = Course.all
  end

  def update
    @video = Video.find(params[:id])
    @video.accessible = :all if admin?
    if @video.update_attributes(params[:video])
      flash[:success] = "Video bijgewerkt"
      redirect_to videos_path
    else
      render 'edit'
    end
  end

  def destroy
    Video.find(params[:id]).destroy
    flash[:success] = "Video verwijderd"
    redirect_to videos_path
  end

  private

    def signed_in_user
      unless signed_in?
        store_location
        redirect_to signin_path, notice: "U moet eerst inloggen."
      end
    end

    def has_rights
      redirect_to(root_path) unless has_permissions?
    end

    def save_view(video)
        @view = View.new
        @view.user_id = current_user.id
        @view.video_id = video.id
        @view.save!
    end
end
