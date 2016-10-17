# == Schema Information
#
# Table name: users
#
#  account_type    :integer          default(0)
#  created_at      :datetime         not null
#  id              :integer          not null, primary key
#  login_code      :integer
#  name            :string(255)
#  password_digest :string(255)
#  remember_token  :string(255)
#  surname         :string(255)
#  updated_at      :datetime         not null
#

class User < ActiveRecord::Base
  attr_accessible :name, :surname, :password, :password_confirmation, :login_code, :account_type, :last_login
  has_secure_password
  has_many :ratings
  has_many :rated_videos, :through => :ratings, :source => :videos
  has_many :views
  has_many :videos, :through => :views
  
  before_save :create_remember_token

  validates :name, presence: true, length: { maximum: 50 }
  validates :surname, presence: true, length: { maximum: 50 }

  validates :login_code, presence: true, uniqueness: true;
  validates_numericality_of :login_code, :only_integer => true, :message => "Allen cijfers zijn toegestaan"


  validates :password, length: { minimum: 6 }, presence: true, :on => :create
  validates :password_confirmation, presence: true, :on => :create

  validates :account_type, presence: true
  validates_inclusion_of :account_type, :in => 0..2, :message => "Selecteer een type" 

  private

    def create_remember_token
      self.remember_token = SecureRandom.urlsafe_base64
    end
end
