class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  before_save :ensure_authentication_token!

  set_rgeo_factory_for_column(:lonlat,
    RGeo::Geographic.spherical_factory(:srid => 4326))

  scope :close_to, -> (target_user, distance_in_meters = 2000) {
   find_by_sql("SELECT * FROM users WHERE ST_DWithin(lonlat::geography, '#{target_user.lonlat}', '#{distance_in_meters}') AND NOT id = #{target_user.id} ")
  }

  # def nearest(count=1)
  #   # order = "lonlat::geometry <-> st_setsrid(st_makepoint(#{lon},#{lat}),4326)"
  #   order = "lonlat::geometry <-> ST_DWithin(st_makepoint(#{lon},#{lat}),4326)"

  #   User.order(order).offset(1).limit(count)
  # end

  scope :all_except, ->(user) { where.not(id: user) }

  def generate_secure_token_string
    SecureRandom.urlsafe_base64(25).tr('lIO0', 'sxyz')
  end

  # Sarbanes-Oxley Compliance: http://en.wikipedia.org/wiki/Sarbanes%E2%80%93Oxley_Act
  # def password_complexity
  #   if password.present? and not password.match(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W]).+/)
  #     errors.add :password, "must include at least one of each: lowercase letter, uppercase letter, numeric digit, special character."
  #   end
  # end

  def password_presence
    password.present? && password_confirmation.present?
  end

  def password_match
    password == password_confirmation
  end

  def ensure_authentication_token!
    if authentication_token.blank?
      self.authentication_token = generate_authentication_token
    end
  end

  def generate_authentication_token
    loop do
      token = generate_secure_token_string
      break token unless User.where(authentication_token: token).first
    end
  end

  def reset_authentication_token!
    self.authentication_token = generate_authentication_token
    self.save
  end

  def lon
    @lon ||= lonlat.try(:x)
  end

  def lat
    @lat ||= lonlat.try(:y)
  end

  before_save do
    self[:lonlat] = "POINT(#{lon} #{lat})"
  end
end
