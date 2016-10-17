class Dashboard::PicturesController < ApplicationController
  layout 'dashboard'
  before_filter :authenticate_user!
  before_filter :is_authorized?, only: [:edit, :update, :destroy]

  def index
    @group = Group.find(params[:group_id])

    @pictures = @group.pictures

    respond_to do |format|
      format.html
      format.json { render json: @pictures }
    end
  end

  def show
    @picture = Picture.find(params[:id])

    respond_to do |format|
      format.html
      format.json { render json: @picture }
    end
  end

  def new
    @group = Group.find(params[:group_id])
    @picture = @group.pictures.build

    respond_to do |format|
      format.html
      format.json { render json: @picture }
    end
  end

  def edit
    @group = Group.find(params[:group_id])
    @picture = @group.pictures.find(params[:id])
    # @picture = Picture.find(params[:id])
  end

  def create
    p_attr = params[:picture]
    p_attr[:image] = params[:picture][:image].first if params[:picture][:image].class == Array

    if params[:group_id]
      @group = Group.find(params[:group_id])
      @picture = @group.pictures.build(p_attr)
    else
      @picture = Picture.new(p_attr)
    end

    if @picture.save
      respond_to do |format|
        format.html {
          render :json => [@picture.to_jq_upload].to_json,
          :content_type => 'text/html',
          :layout => false
        }
        format.json {
          render :json => {files: [@picture.to_jq_upload]}.to_json
        }
      end
    else
      render :json => [{:error => "custom_failure"}], :status => 304
    end
  end

  def update
    @group = Group.find(params[:group_id])

    @picture = @group.pictures.find(params[:id])

    respond_to do |format|
      if @picture.update_attributes(params[:picture])
        format.html { redirect_to edit_dashboard_group_path(@group), notice: 'Afbeelding is succesvol bijgewerkt.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @picture.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @group = Group.find(params[:group_id])
    @picture = @group.pictures.find(params[:id])
    @picture.destroy

    respond_to do |format|
      format.html { redirect_to group_pictures_url }
      format.js
    end
  end
end

private
  def is_authorized?
    @group = Group.find(params[:id])
    unless current_user.is_admin? || @group.users.include?(current_user)
      redirect_to dashboard_groups_path, alert: 'U heeft hier niet de rechten voor.'
    end
  end