module VideosHelper
require 'uri'

    def videoId(video)
        uri = URI.parse(video)
        a = uri.query
        a.slice!(0,2)
        id = a.split("&")[0]
        return id
    end

    def rating_ballot
        if @rating = current_user.ratings.find_by_video_id(params[:id])
            @rating
        else
            current_user.ratings.new
        end
    end

    def current_user_rating
        if @rating = current_user.ratings.find_by_video_id(params[:id])
            @rating.value
        else
            "N/A"
        end
    end

end
