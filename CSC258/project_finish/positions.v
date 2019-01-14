module pillar_pos(
	input clk, reset_n, game_reset, enable,
	input [7:0] load_x_n,
	input [3:0] y_gap,
	output reg [7:0] x_pos, 
	output reg [6:0] y_gap_pos
	);

	// pos control
	always @(posedge clk) begin
		if (!reset_n | !game_reset) begin
			y_gap_pos <= y_gap;
			x_pos <= load_x_n;
		end
		else if (enable) begin
			if (x_pos == -8'd24) begin
				x_pos <= 8'd160;
				y_gap_pos <= {1'b0, y_gap, 2'b0} + 7'd8;
			end
			else begin
				x_pos = x_pos - 8'd1;
			end
		end
	end
endmodule


module bird_pos(
	input clk, reset_n, game_reset, enable,
	input dir_enable,
	output reg [6:0] y_pos
	);
	
	reg [3:0] up, down;
	reg [1:0] enabled;
	
	// direction control
	always @(posedge clk) begin
		if (!reset_n | !game_reset) begin
			down <= 4'd0;
			up <= 4'd0;
			enabled <= 2'd2;
		end
		else if (enable == 1'b1) begin
			if (enabled > 0)
				enabled = enabled - 1'b1;
			else begin
				if (down < 4'd4) begin
					down = down + 4'd1;
				end
			end
			if (up > 0) 
				up = up - 4'd1;
			else if (up < 12 & dir_enable) begin
				up = up + 4'd8;
				down <= 4'd0;
			end
		end
	end
	
	// pos control
	always @(posedge clk) begin
		if (!reset_n) begin
			y_pos <= 7'd50;
			
		end
		else if (enable == 1'b1) begin
			if (y_pos - up + down > 7'd2 & y_pos - up + down < 7'd118) 
				y_pos = y_pos - up + down;
		end
	end
endmodule