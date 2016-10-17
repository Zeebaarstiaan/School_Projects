class RatingsController < ApplicationController
    before_filter :signed_in_user

    def create
        @video = Video.find_by_id(params[:video_id])
        @rating = Rating.new(params[:rating])
        @rating.video_id = @video.id
        @rating.user_id = current_user.id
        if @rating.save
            respond_to do |format|
                format.html { redirect_to video_path(@video), :notice => "Your rating has been saved" }
                format.js
            end
        end
    end

    def update
        @video = Video.find_by_id(params[:video_id])  
        @rating = current_user.ratings.find_by_video_id(@video.id)
        if @rating.update_attributes(params[:rating])
            respond_to do |format|
                format.html { redirect_to video_path(@video), :notice => "Your rating has been updated" }
                format.js
            end
        end
    end

  private
    def signed_in_user
      unless signed_in?
        store_location
        redirect_to signin_path, notice: "U moet eerst inloggen."
      end
    end
end
