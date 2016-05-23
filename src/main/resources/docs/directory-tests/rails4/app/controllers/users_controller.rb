##Patterns: FileAccess,SkipBeforeFilter,Evaluation,CreateWith
class UsersController < ApplicationController
  def test_sql_sanitize
    User.where("age > #{sanitize params[:age]}")
  end

  before_action :set_page

  prepend_before_action :safe_set_page, :only => :test_prepend_before_action
  append_before_action :safe_set_page, :only => :test_append_before_action

  ##Warn: SkipBeforeFilter
  skip_before_action :verify_authenticity_token, :except => [:unsafe_stuff]

  def test_before_action
    render @page
  end

  # Call safe_set_page then set_page
  def test_prepend_before_action
    render @page # should not be safe
  end

  # Call set_page then safe_set_page
  def test_append_before_action
    render @page # should be safe
  end

  def set_page
    @page = params[:page]
  end

  def safe_set_page
    @page = :cool_page_bro
  end

  def redirect_to_model
    # None of these should warn in Rails 4
    if stuff
      redirect_to User.find_by(:name => params[:name])
    elsif other_stuff
      redirect_to User.find_by!(:name => params[:name])
    else
      redirect_to User.where(:stuff => 1).take
    end
  end

  def find_by_stuff
    User.find_by "age > #{params[:age_limit]}"
    User.find_by! params[:user_search]
  end

  def symbolize_safe_parameters
    params[:controller].to_sym
    params[:action].intern && params[:controller][/([^\/]+)$/].try(:to_sym)
  end

  def mass_assignment_bypass
    ##Warn: CreateWith
    User.create_with(params)  # high warning
    ##Warn: CreateWith
    User.create_with(params).create # high warning
    User.create_with(params[:x].permit(:y)) # should not warn, workaround
    something.create_with({}) # should not warn on hash literals
    ##Warn: CreateWith
    x.create_with(y(params))  # medium warning
    ##Warn: CreateWith
    y.create_with(x)          # weak warning
  end

  def email_finds
    Email.find_by_id! params[:email][:id]
  end

  def case_statement
    @x = case params[:x]
         when :yes
           "yep"
         when :no
           "nope"
         else
           "dunno"
         end
  end

  def open_stuff
    ##Warn: FileAccess
    open(params[:url]) # remote code execution warning
    ##Warn: FileAccess
    Kernel.open(URI(params[:url])) # file access and RCE warning
    ##Warn: FileAccess
    open("#{params[:x]}/something/something") # remote code execution warning
    ##Warn: FileAccess
    open("some_path/#{params[:x]}/something/something") # file access warning
  end

  def eval_it
    ##Warn: Evaluation
    @x = eval(params[:x])
  end
end
