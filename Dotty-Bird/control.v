module control(
    input clk, reset_n, start, 
	input f_plot, f_wait, fail, 
	output reg en_wait, en_plot,
	output reg en_move,
    output reg ld_sw, ld_erase, ld_rs,
	output reg reset_wait, reset_plot,
    output reg writeEn, game_reset
    );

    reg [3:0] current_state, next_state; 
	 
    localparam  S_INIT = 4'd0, 
				S_CLEAR = 4'd7,
				S_LOAD_PLOT = 4'd1,
				S_PLOT = 4'd2,
				S_LOAD_ERASE = 4'd4,
				S_ERASE = 4'd5,
				S_MOVE = 4'd6;
				
    always@(*)
    begin: state_table 
            case (current_state)
                S_INIT: next_state = start ? S_CLEAR : S_INIT;
				S_CLEAR: next_state = f_wait ? S_LOAD_PLOT : S_CLEAR;
					 
				S_LOAD_PLOT: next_state = S_PLOT;
				S_PLOT: next_state = f_wait ? S_LOAD_ERASE: S_PLOT;
				S_LOAD_ERASE: next_state = S_ERASE;
				S_ERASE: next_state = f_plot ? S_MOVE : S_ERASE;
				S_MOVE: next_state = S_LOAD_PLOT;
            default:   next_state = S_INIT;
        endcase
    end // state_table
   

    always @(*) begin: enable_signals
        ld_sw = 1'b0;
		ld_rs = 1'b0;
		ld_erase = 1'b0;
        writeEn = 1'b0;
		en_move = 1'b0;
		reset_plot = 1'b0;
		reset_wait = 1'b0;
		en_plot = 1'b0;
		en_wait = 1'b0;
		game_reset = 1'b1;

        case (current_state)
			S_INIT: begin
				ld_rs = 1'b1;
				game_reset = 1'b0;
			end
			S_CLEAR: begin
				writeEn = 1'b1;
				reset_wait = 1'b1;
				en_wait = 1'b1;
			end
			S_LOAD_PLOT: begin
				ld_sw = 1'b1;
			end
			S_PLOT: begin
				writeEn = 1'b1;
				reset_wait = 1'b1;
				en_wait = 1'b1; 
			end
			S_LOAD_ERASE: begin
				ld_sw = 1'b1;
				ld_erase = 1'b1;
			end
			S_ERASE: begin
				writeEn = 1'b1;
				reset_plot = 1'b1;
				en_plot = 1'b1;
				ld_erase = 1'b1;
			end
			S_MOVE: begin
				en_move = 1'b1;
			end	
        endcase
    end // enable_signals
	
    // current_state registers
    always@(posedge clk) begin
        if(!reset_n)
            current_state <= S_INIT;
		  else if (fail)
				current_state <= S_INIT;
        else
            current_state <= next_state;
    end // state_FFS
endmodule