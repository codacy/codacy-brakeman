##Patterns: RenderInline,RegexDoS
class AnotherController < ApplicationController
  def overflow
    @test = @test.where.all
  end

  before_filter do
    eval params[:x]
  end

  skip_before_action :set_bad_thing, :except => [:also_use_bad_thing]

  def use_bad_thing
    # This should not warn, because the filter is skipped!
    User.where(@bad_thing)
  end

  def also_use_bad_thing
    `#{@bad_thing}`
  end

  def render_stuff
    user_name = User.current_user.name

    ##Warn: RenderInline
    render :text => "Welcome back, #{params[:name]}!}"
    ##Warn: RenderInline
    render :text => "Welcome back, #{user_name}!}"
    ##Warn: RenderInline
    render :text => params[:q]
    ##Warn: RenderInline
    render :text => user_name

    ##Warn: RenderInline
    render :inline => "<%= #{params[:name]} %>"
    ##Warn: RenderInline
    render :inline => "<%= #{user_name} %>"

    # should not warn
    render :text => CGI.escapeHTML(params[:q])
    render :text => "Welcome back, #{CGI::escapeHTML(params[:name])}!}"
  end

  def use_params_in_regex
    ##Warn: RegexDoS
    @x = something.match /#{params[:x]}/
  end

  def building_strings_for_sql
    query = "SELECT * FROM users WHERE"

    if params[:search].to_i == 1
      query << " role = 'admin'"
    else
      query << " role = 'admin' " + params[:search]
    end

    begin
      result = {:result => User.find_by_sql(query) }
    rescue
      result = {}
    end

    render json: result.as_json
  end
end
