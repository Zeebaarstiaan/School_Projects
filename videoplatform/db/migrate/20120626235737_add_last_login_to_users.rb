class AddLastLoginToUsers < ActiveRecord::Migration
  def change
    add_column :users, :last_login, :timestamp
  end
end
