class StaticPagesController < ApplicationController
  def home
    @courses = Course.all
  end

  def help
  end

  def about
  end

  def contact
  end
end
