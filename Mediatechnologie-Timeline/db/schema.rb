# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20130618204950) do

  create_table "clients", :force => true do |t|
    t.string   "name"
    t.text     "description"
    t.string   "site"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
  end

  create_table "clients_groups", :id => false, :force => true do |t|
    t.integer "client_id"
    t.integer "group_id"
  end

  add_index "clients_groups", ["client_id", "group_id"], :name => "index_clients_groups_on_client_id_and_group_id"

  create_table "college_years", :force => true do |t|
    t.integer  "start_year", :limit => 4
    t.datetime "created_at",              :null => false
    t.datetime "updated_at",              :null => false
  end

  create_table "college_years_projects", :id => false, :force => true do |t|
    t.integer "college_year_id"
    t.integer "project_id"
  end

  add_index "college_years_projects", ["college_year_id", "project_id"], :name => "index_college_years_projects_on_college_year_id_and_project_id"

  create_table "groups", :force => true do |t|
    t.string   "title"
    t.text     "description"
    t.text     "content"
    t.integer  "project_id"
    t.integer  "college_year_id"
    t.boolean  "archived",        :default => false
    t.boolean  "approved",        :default => false
    t.datetime "created_at",                         :null => false
    t.datetime "updated_at",                         :null => false
  end

  create_table "groups_users", :id => false, :force => true do |t|
    t.integer "group_id"
    t.integer "user_id"
  end

  add_index "groups_users", ["group_id", "user_id"], :name => "index_groups_users_on_group_id_and_user_id"

  create_table "pictures", :force => true do |t|
    t.string   "description"
    t.string   "image"
    t.integer  "crop_x"
    t.integer  "crop_y"
    t.integer  "crop_w"
    t.integer  "crop_h"
    t.integer  "imageable_id"
    t.string   "imageable_type"
    t.datetime "created_at",     :null => false
    t.datetime "updated_at",     :null => false
  end

  create_table "projects", :force => true do |t|
    t.string   "title"
    t.text     "description"
    t.integer  "school_year"
    t.string   "preview"
    t.boolean  "highlight",   :default => false
    t.datetime "created_at",                     :null => false
    t.datetime "updated_at",                     :null => false
  end

  create_table "stickers", :force => true do |t|
    t.string   "title"
    t.text     "description"
    t.string   "link"
    t.integer  "project_id"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
  end

  create_table "users", :force => true do |t|
    t.string   "email",                               :default => "",    :null => false
    t.string   "encrypted_password",                  :default => "",    :null => false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",                       :default => 0
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip"
    t.string   "last_sign_in_ip"
    t.boolean  "admin",                               :default => false
    t.string   "first_name"
    t.string   "last_name"
    t.string   "student_number",         :limit => 7
    t.datetime "starting_year"
    t.datetime "string"
    t.string   "portfolio"
    t.text     "description"
    t.string   "avatar"
    t.datetime "created_at",                                             :null => false
    t.datetime "updated_at",                                             :null => false
  end

  add_index "users", ["email"], :name => "index_users_on_email", :unique => true
  add_index "users", ["reset_password_token"], :name => "index_users_on_reset_password_token", :unique => true

end
