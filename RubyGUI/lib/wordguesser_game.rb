class WordGuesserGame

  # add the necessary class methods, attributes, etc. here
  # to make the tests in spec/wordguesser_game_spec.rb pass.

  # Get a word from remote "random word" service

  def initialize(word)
    @word = word.downcase
    @word_with_guesses = ''
    for i in 1..word.length
      @word_with_guesses += '-'
    end
    @guesses = ''
    @wrong_guesses = ''
    @number_of_guesses = 0
    @double_letter = false
    @invalid_input = false
  end

  attr_accessor :invalid_input, :double_letter, :word, :word_with_guesses, :guesses, :wrong_guesses, :number_of_guesses

  # You can test it by installing irb via $ gem install irb
  # and then running $ irb -I. -r app.rb
  # And then in the irb: irb(main):001:0> WordGuesserGame.get_random_word
  #  => "cooking"   <-- some random word
  def self.get_random_word
    require 'uri'
    require 'net/http'
    uri = URI('http://randomword.saasbook.info/RandomWord')
    Net::HTTP.new('randomword.saasbook.info').start { |http|
      return http.post(uri, "").body
    }
  end

  def word_with_guesses
    return @word_with_guesses
  end

  def guess(letter)
    if letter == nil
      raise ArgumentError.new("Guess can not be nil")
      #@invalid_input = true
      return false
    end
    letter.downcase!
    if letter == nil
      raise ArgumentError.new("Guess can not be nil")
      #@invalid_input = true
      return false
    end
    if letter =~ /\A[a-z]\z/
      if @word.include? letter
        if !(@guesses.include? letter)
          @guesses += letter
          for x in 0..@word.length
            if @word[x] == letter
              @word_with_guesses[x] = letter
            end
          end
        else
          @double_letter = true
          return false
        end
      else
        if !(@wrong_guesses.include? letter)
          @number_of_guesses += 1
          @wrong_guesses += letter
        else
          @double_letter = true
          return false
        end
      end
    else
      @invalid_input = true
      raise ArgumentError.new("Not valid letter guess, must be an alphabet letter")
      return false
    end
  end

  def check_win_or_lose
    if @number_of_guesses < 7 
      if @word_with_guesses == @word
        return :win
      else return :play
      end
    else return :lose
    end
  end

end
