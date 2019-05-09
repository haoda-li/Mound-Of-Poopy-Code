module collision_detection(
	input clk, reset_n, game_reset,
	input [7:0] pillar_1_x, pillar_2_x, pillar_3_x,
	input [6:0] bird_y, gap_1_y, gap_2_y, gap_3_y,
	output reg fail
);
	always @(posedge clk) begin
		if (!reset_n) begin
			fail = 1'b0;
		end
		else begin
			if (pillar_1_x > 8'd0 && pillar_1_x < 8'd24) begin
				if (gap_1_y > bird_y - 7'd1 || gap_1_y + 7'd36 < bird_y) 
					fail = 1'b1;
			end
			else if (pillar_2_x > 8'd0 && pillar_2_x < 8'd24) begin
				if (gap_2_y > bird_y - 7'd1 || gap_2_y + 7'd36 < bird_y) 
					fail = 1'b1;
			end
			else if (pillar_3_x > 8'd0 && pillar_3_x < 8'd24) begin
				if (gap_3_y > bird_y - 7'd1 || gap_3_y + 7'd36 < bird_y) 
					fail = 1'b1;
			end
			else 
				fail = 1'b0;
		end
	end
	
endmodule

module randomgenerator(
	input clk, reset_n, 
	input [3:0] seed, 
	output reg [3:0] fib1
);
	
	always @(posedge clk) begin
		if (!reset_n)
			fib1 <= seed;
		else
			fib1 <= {fib1[3]^fib1[2], fib1[2:0]};
	end
endmodule

module scoreboard(
	input clk, reset_n, game_reset, enable,
	input [7:0] pillar_1_x, pillar_2_x, pillar_3_x,
	input [6:0] bird_y, gap_1_y, gap_2_y, gap_3_y,
	output reg [6:0] digit1, digit2
);
	reg [6:0] score1, score2;
	always @(posedge clk) begin
		if (!reset_n || !game_reset) begin
			score1 = 4'd0;
			score2 = 4'd0;
		end
		else if (enable) begin
			if (pillar_1_x == 8'd14) begin
				if (gap_1_y <= bird_y - 7'd1 && gap_1_y + 7'd36 >= bird_y) begin
					if (score2 != 4'd9) 
						score2 = score2 + 4'd1;
					else begin
						score1 = score1 + 4'd1;
						score2 = 4'd0;
					end
				end
			end
			else if (pillar_2_x == 8'd14) begin
				if (gap_2_y <= bird_y - 7'd1 && gap_2_y + 7'd36 >= bird_y) begin
					if (score2 != 4'd9) 
						score2 = score2 + 4'd1;
					else begin
						score1 = score1 + 4'd1;
						score2 = 4'd0;
					end
				end
			end
			else if (pillar_3_x == 8'd14) begin
				if (gap_3_y <= bird_y - 7'd1 && gap_3_y + 7'd36 >= bird_y) begin
					if (score2 != 4'd9) 
						score2 = score2 + 4'd1;
					else begin
						score1 = score1 + 4'd1;
						score2 = 4'd0;
					end
				end
			end
		end
	end
	
	always @(*) begin
		case (score1)
			7'd0: digit1 = 7'b0000001;
			7'd1: digit1 = 7'b1001111; 
			7'd2: digit1 = 7'b0010010; 
			7'd3: digit1 = 7'b0000110;
			7'd4: digit1 = 7'b1001100;
			7'd5: digit1 = 7'b0100100;
			7'd6: digit1 = 7'b0100000;
			7'd7: digit1 = 7'b0001111;
			7'd8: digit1 = 7'b0000000;
			7'd9: digit1 = 7'b0001100;
		endcase
		case (score2)
			7'd0: digit2 = 7'b0000001;
			7'd1: digit2 = 7'b1001111; 
			7'd2: digit2 = 7'b0010010; 
			7'd3: digit2 = 7'b0000110;
			7'd4: digit2 = 7'b1001100;
			7'd5: digit2 = 7'b0100100;
			7'd6: digit2 = 7'b0100000;
			7'd7: digit2 = 7'b0001111;
			7'd8: digit2 = 7'b0000000;
			7'd9: digit2 = 7'b0001100;
		endcase
	end
endmodule

