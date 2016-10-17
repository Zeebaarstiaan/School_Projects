class BannerUploader < CarrierWave::Uploader::Base
  include CarrierWave::RMagick

  include Sprockets::Helpers::RailsHelper
  include Sprockets::Helpers::IsolatedHelper

  storage :file

  def store_dir
    "uploads/#{model.class.to_s.underscore}/#{mounted_as}/#{model.id}"
  end

  def default_url
   asset_path([version_name, "default_preview.png"].compact.join('_'))
  end

  version :small_project do
    resize_to_fill(140, 140)
  end

  version :large_project do
    resize_to_fill(300, 300)
  end

  version :horizontal_project do
    resize_to_fill(300, 140)
  end

  version :vertical_project do
    resize_to_fill(140, 300)
  end

  version :banner do
    resize_to_fill(550, 110)
  end

  version :thumb do
    resize_to_fill(100, 100)
  end

end
