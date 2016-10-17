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

require 'spec_helper'

describe User do
  pending "add some examples to (or delete) #{__FILE__}"
end
