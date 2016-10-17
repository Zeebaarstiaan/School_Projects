require 'rubygems'
require 'sinatra'
require 'data_mapper'
require 'time'
require 'rack-flash'
require 'active_support/core_ext'
require 'active_support/inflector'
require 'sinatra/redirect_with_flash'
require 'json'
require 'rabl'
require 'builder'
require "sinatra/reloader" if development?

SITE_TITLE = "Movie Reviews"
SITE_DESCRIPTION = "Because bad movies can't be unseen"

enable :sessions
use Rack::Flash, :sweep => true

DataMapper.setup(:default, ENV['DATABASE_URL'] || "sqlite3://#{Dir.pwd}/movie_reviews.db")

class Movie
	include DataMapper::Resource
	property :id, Serial
	property :title, String, :required => true, :length => 50
	property :description, Text, :required => true
	property :released, Integer, :required => true, :auto_validation => true
	property :created_at, DateTime
	property :updated_at, DateTime

	has n, :reviews
end

class Review
	include DataMapper::Resource
	property :id, Serial
	property :rating, Boolean, :required => true
	property :comment, Text, :required => true
	property :created_at, DateTime
	property :updated_at, DateTime

	belongs_to :movie
end

DataMapper.auto_upgrade!
Rabl.register!

helpers do
	include Rack::Utils
	alias_method :h, :escape_html
end

#
# Application
#


# // Content Types \\ #
get '/movies.:file_type' do
	@movies = Movie.all :order => :id
	@title = 'All movies'
	if @movies.empty?
		halt 404, 'No movies found, please <a href="/movies/add">add one</a>'
	end
	if params[:file_type] == "xml"
		headers 'Content-Type' => 'text/xml'
		render :rabl, :movies, :format => 'xml'
	elsif params[:file_type] == "json"
		headers 'Content-Type' => 'application/json'
		render :rabl, :movies, :format => 'json'
	else
		redirect '/movies'
	end		
end

get '/movies/:id.:file_type' do
	@movie = Movie.get params[:id]
	@title = "#{params[:title]}"
	if !@movie
		halt 404, 'Movie not found. <a href="/movies">Back to movie-list.</a>'
	end
	if params[:file_type] == "xml"
		headers 'Content-Type' => 'text/xml'
		render :rabl, :movie, :format => 'xml'
	elsif params[:file_type] == "json"
		headers 'Content-Type' => 'application/json'
		render :rabl, :movie, :format => 'json'
	else
		redirect "/movies/#{params[:id]}"
	end			
end

get '/movies/:id/reviews.:file_type' do
	@movie = Movie.get params[:id]
	@reviews = @movie.reviews.all
	@title = 'All reviews'
	if @reviews.empty?
		halt 404, "No reviews found for this movie, please <a href=\"/movies/#{@movie.id}/reviews/add\">add one</a>, or <a href=\"/movies/#{@movie.id}\">go back.</a>"
	end
	
	if params[:file_type] == "xml"
		headers 'Content-Type' => 'text/xml'
		render :rabl, :reviews, :format => 'xml'
	elsif params[:file_type] == "json"
		headers 'Content-Type' => 'application/json'
		render :rabl, :reviews, :format => 'json'
	else
		redirect "/movies/#{params[:id]}/reviews"
	end			
end

get '/movies/:id_user/reviews/:id_review.:file_type' do
	@movie = Movie.get params[:id_user]
	@review = @movie.reviews.get params[:id_review]
	@title = "#{@movie.title} review ##{@review.id}"
	if !@review
		halt 404, "Movie-review not found. <a href=\"/movies/#{params[:id_user]}/reviews\">Back to review-list.</a>"
	end

	if params[:file_type] == "xml"
		headers 'Content-Type' => 'text/xml'
		render :rabl, :review, :format => 'xml'
	elsif params[:file_type] == "json"
		headers 'Content-Type' => 'application/json'
		render :rabl, :review, :format => 'json'
	else
		redirect "/movies/#{params[:id_user]}/reviews/#{params[:id_review]}"
	end			
end


# // Root \\ #

get '/' do
  status 418
  headers \
    "Allow"   => "GET"
    body 'Get the documentation <a href="/STR6 Webservice - Sebastiaan Scheers - 0836947.pdf">here</a><br>Or visit the html views <a href="/movies">here</a>'
end


post '/' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/' do
  status 405
  headers \
    "Allow"   => "GET"
end

# // Movies \\ #
get '/movies&:query' do
	@movies = Movie.all(:title.like => "%#{params[:query]}%")
	@title = 'All movies'
	if @movies.empty?
		halt 404, '<h1>404</h1><p>Not Found</p>No movies found, please <a href="/movies/add">add one</a>'
	end
	erb :movies
end

get '/movies' do
	@movies = Movie.all :order => :id
	@title = 'All movies'
	if @movies.empty?
		halt 404, 'No movies found, please <a href="/movies/add">add one</a>'
	end
	erb :movies
end

put '/movies' do
  status 405
  headers \
    "Allow"   => "GET,POST"
end

delete '/movies' do
  status 405
  headers \
    "Allow"   => "GET,POST"
end

get '/movies/add' do
	@title = 'Add movie'
	erb :add_movie
end

post '/movies/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/movies/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

post '/movies' do
	m = Movie.new
	m.attributes = {
		:title => params[:title],
		:released => params[:released],
		:description => params[:description],
		:created_at => Time.now,
		:updated_at => Time.now
	}
	if m.save
		redirect "/movies/#{m.id}", :notice => 'Movie created successfully.'
	else
		redirect '/movies/add', :error => 'Failed to save movie.'
	end
end

get '/movies/:id' do
	@movie = Movie.get params[:id]
	@title = "#{params[:title]}"
	if @movie
		erb :movie
	else
		halt 404, 'Movie not found. <a href="/movies">Back to movie-list.</a>'
	end
end

post '/movies/:id' do
  status 405
  headers \
    "Allow"   => "GET,PUT,DELETE"
end


get '/movies/:id/edit' do
	@movie = Movie.get params[:id]
	@title = "Edit #{params[:title]}"
	if @movie
		erb :edit_movie
	else
		halt 404, 'Movie not found. <a href="/movies">Back to movie-list.</a>'
	end
end

post '/movies/:id/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/movies/:id/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/movies/:id' do
	m = Movie.get params[:id]
	unless m
		halt 404, 'Movie not found. <a href="/movies">Back to movie-list.</a>'
	end
	m.attributes = {
		:title => params[:title],
		:released => params[:released],
		:description => params[:description],
		:updated_at => Time.now
	}
	if m.save
		redirect "/movies/#{m.id}", :notice => 'movie updated successfully.'
	else
		redirect "/movies/#{m.id}", :error => 'Error updating movie.'
	end
end

get '/movies/:id/delete' do
	@movie = Movie.get params[:id]
	@title = "Delete #{params[:title]}"
	if @movie
		erb :delete_movie
	else
		halt 404, 'Movie not found. <a href="/movies">Back to movie-list.</a>'
	end
end

post '/movies/:id/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/movies/:id/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id' do
	m = Movie.get params[:id]
	if m.destroy
		redirect '/movies', :notice => 'Movie deleted successfully.'
	else
		redirect "/movies/#{m.id}/delete", :error => 'Error deleting movie.'
	end
end


# // Reviews \\ #
get '/movies/:id/reviews&:query' do
	@movie = Movie.get params[:id]
	if params[:query] == "bad"
		@reviews = @movie.reviews.all(:rating => false)
	else
		@reviews = @movie.reviews.all(:rating => true)
	end
	@title = 'All reviews'
	if @reviews.empty?
		halt 404, "No reviews found for this movie, please <a href=\"/movies/#{@movie.id}/reviews/add\">add one</a>, or <a href=\"/movies/#{@movie.id}\">go back.</a>"
	end
	erb :reviews
end

get '/movies/:id/reviews' do
	@movie = Movie.get params[:id]
	@reviews = @movie.reviews.all
	@title = 'All reviews'
	if @reviews.empty?
		halt 404, "No reviews found for this movie, please <a href=\"/movies/#{@movie.id}/reviews/add\">add one</a>, or <a href=\"/movies/#{@movie.id}\">go back.</a>"
	end
	erb :reviews
end

put '/movies/:id/reviews' do
  status 405
  headers \
    "Allow"   => "GET,POST"
end

delete '/movies/:id/reviews' do
  status 405
  headers \
    "Allow"   => "GET,POST"
end

get '/movies/:id/reviews/add' do
	@movie = Movie.get params[:id]
	@title = 'Add movie review'
	erb :add_review
end

put '/movies/:id/reviews/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

post '/movies/:id/reviews/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id/reviews/add' do
  status 405
  headers \
    "Allow"   => "GET"
end

post '/movies/:id/reviews' do
	@movie = Movie.get params[:id]
	r = @movie.reviews.new
	r.attributes = {
		:rating => params[:rating],
		:comment => params[:comment],
		:movie_id => @movie.id,
		:created_at => Time.now,
		:updated_at => Time.now
	}
	if r.save
		redirect "/movies/#{params[:id]}/reviews/#{r.id}", :notice => 'Movie-review created successfully.'
	else
		redirect  "/movies/#{params[:id]}/reviews", :error => 'Failed to save movie-review.'
	end
end

get '/movies/:id_user/reviews/:id_review' do
	@movie = Movie.get params[:id_user]
	@review = @movie.reviews.get params[:id_review]
	@title = "#{@movie.title} review ##{@review.id}"
	if @review
		erb :review
	else
		halt 404, "Movie-review not found. <a href=\"/movies/#{params[:id_user]}/reviews\">Back to review-list.</a>"
	end
end

post '/movies/:id/reviews/:id_review' do
  status 405
  headers \
    "Allow"   => "GET,PUT,DELETE"
end

get '/movies/:id_user/reviews/:id_review/edit' do
	@movie = Movie.get params[:id]
	@movie = Movie.get params[:id_user]
	@review = @movie.reviews.get params[:id_review]
	@title = "Edit #{@movie.title} review ##{@review.id}"
	if @review
		erb :edit_review
	else
		halt 404, "Movie-review not found. <a href=\"/movies/#{params[:id_user]}/reviews\">Back to review-list.</a>"
	end
end

put '/movies/:id_user/reviews/:id_review/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

post '/movies/:id_user/reviews/:id_review/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id_user/reviews/:id_review/edit' do
  status 405
  headers \
    "Allow"   => "GET"
end

put '/movies/:id_user/reviews/:id_review' do
	@movie = Movie.get params[:id_user]
	r = @movie.reviews.get params[:id_review]
	unless r
		halt 404, "Movie-review not found. <a href=\"/movies/#{params[:id_user]}/reviews\">Back to review-list.</a>"
	end
	r.attributes = {
		:rating => params[:rating],
		:comment => params[:comment],
		:movie_id => @movie.id,
		:updated_at => Time.now
	}
	if r.save
		redirect "/movies/#{params[:id_user]}/reviews/#{r.id}", :notice => 'Movie-review updated successfully.'
	else
		redirect  "/movies/#{params[:id_user]}/reviews/#{r.id}/edit", :error => 'Failed to update movie-review.'
	end
end

get '/movies/:id_user/reviews/:id_review/delete' do
	@movie = Movie.get params[:id_user]
	@review = @movie.reviews.get params[:id_review]
	@title = "Delete #{@movie.title} review ##{@review.id}"
	if @review
		erb :delete_review
	else
		halt 404, "Movie-review not found. <a href=\"/movies/#{params[:id_user]}/reviews\">Back to review-list.</a>"
	end
end

put '/movies/:id_user/reviews/:id_review/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

post '/movies/:id_user/reviews/:id_review/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id_user/reviews/:id_review/delete' do
  status 405
  headers \
    "Allow"   => "GET"
end

delete '/movies/:id_user/reviews/:id_review' do
	@movie = Movie.get params[:id_user]
    r = @movie.reviews.get params[:id_review]
	if r.destroy
		redirect "/movies/#{params[:id_user]}/reviews", :notice => 'Movie-review deleted successfully.'
	else
		redirect "/movies/#{m.id}/delete", :error => 'Error deleting movie-review.'
	end
end


