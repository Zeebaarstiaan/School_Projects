class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :confirmable,
  # :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  attr_accessible :email, :password, :password_confirmation, :remember_me, :first_name, :last_name, :admin, :student_number, :starting_year, :portfolio, :avatar, :remove_avatar, :description

  has_and_belongs_to_many :groups
  mount_uploader :avatar, AvatarUploader

  def is_admin?
    admin
  end

  def name
    "#{first_name} #{last_name}"
  end

  def owner?(project)
    if self.admin? || project.users.include?(self)
      return true
    else
      return false
    end
  end

  def starting_year_display
    starting_year.strftime("%Y")
  end

end
