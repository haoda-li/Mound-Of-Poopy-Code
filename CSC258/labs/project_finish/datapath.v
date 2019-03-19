module datapath(
        input clk, reset_n, 
        input [7:0] pillar_1_x, pillar_2_x, pillar_3_x, 
		input [6:0] bird_y, gap_1_y, gap_2_y, gap_3_y, 
		input [6:0] digit1, digit2, 
        input ld_erase, ld_sw, ld_rs, enable,
        output [7:0] x_out,
        output [6:0] y_out,
        output reg [2:0] colour
    );
    
    reg [7:0] x_count, length, x;
	reg [6:0] y_count, y;
    reg [4:0] switch;
	reg ld_in;
	reg [6:0] score1, score2;
							 
	localparam 	S_PILLAR_1_T = 5'd0, 
				S_PILLAR_2_T = 5'd1, 
				S_PILLAR_3_T = 5'd2, 
				S_PILLAR_1_B = 5'd3,
				S_PILLAR_2_B = 5'd4,
				S_PILLAR_3_B = 5'd5,
				S_BIRD = 5'd6,
				
				S_SEG_1 = 5'd7,
				S_SEG_2 = 5'd8,
				S_SEG_3 = 5'd9,
				S_SEG_4 = 5'd10,
				S_SEG_5 = 5'd11,
				S_SEG_6 = 5'd12,
				S_SEG_7 = 5'd13,
				
				S_SEG_8 = 5'd14,
				S_SEG_9 = 5'd15,
				S_SEG_10 = 5'd16,
				S_SEG_11 = 5'd17,
				S_SEG_12 = 5'd18,
				S_SEG_13 = 5'd19,
				S_SEG_14 = 5'd20,
				
				S_BACKGROUND = 5'd21;
				
				
	
    always @(posedge clk) begin
        if (!reset_n) begin
            x <= 8'b0;
			y <= 7'b0;
			length <= 8'b0;
            x_count <= 8'b0;
			y_count <= 7'b0;
            colour <= 3'b0;
        end
		else if (ld_rs) begin
			switch <= S_BACKGROUND;
			ld_in <= 1'b1;
		end
		else if (ld_sw) begin
			switch <= S_PILLAR_1_T;
			ld_in <= 1'b1;
			score1 <= digit1;
			score2 <= digit2;
		end
        else if (ld_in) begin
            case (switch)
				S_BIRD: begin
					x = 8'd20;
					y = bird_y;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd3;
					colour = ld_erase ? 3'b000 : 3'b011;
				end
				S_PILLAR_1_T: begin
					x = ld_erase ? pillar_1_x + 8'd19 : pillar_1_x;
					y = 7'd0;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = gap_1_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_PILLAR_2_T: begin
					x = ld_erase ? pillar_2_x + 8'd19 : pillar_2_x;
					y = 7'd0;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = gap_2_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_PILLAR_3_T: begin
					x = ld_erase ? pillar_3_x + 8'd19 : pillar_3_x;
					y = 7'd0;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = gap_3_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_PILLAR_1_B: begin
					x = ld_erase ? pillar_1_x + 8'd19 : pillar_1_x;
					y = gap_1_y + 7'd40;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = 7'd80 - gap_1_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_PILLAR_2_B: begin
					x = ld_erase ? pillar_2_x + 8'd19 : pillar_2_x;
					y = gap_2_y + 7'd40;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = 7'd80 - gap_2_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_PILLAR_3_B: begin
					x = ld_erase ? pillar_3_x + 8'd19 : pillar_3_x;
					y = gap_3_y + 7'd40;
					length = ld_erase ? 8'd1 : 8'd19;
					x_count = ld_erase ? 8'd1 : 8'd19;
					y_count = 7'd80 - gap_3_y;
					colour = ld_erase ? 3'b000 : 3'b110;
				end
				S_BACKGROUND: begin
					x = 8'd0;
					y = 7'd0;
					length = 8'd159;
					x_count = 8'd159;
					y_count = 7'd119;
					colour = 3'b000;
				end
				
				S_SEG_1: begin
					x = 8'd131;
					y = 7'd10;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score1[6] ? 3'b000 : 3'b111;
				end
				S_SEG_2: begin
					x = 8'd135;
					y = 7'd11;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score1[5] ? 3'b000 : 3'b111;
				end
				S_SEG_3: begin
					x = 8'd135;
					y = 7'd16;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score1[4] ? 3'b000 : 3'b111;
				end
				S_SEG_4: begin
					x = 8'd131;
					y = 7'd20;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score1[3] ? 3'b000 : 3'b111;
				end
				S_SEG_5: begin
					x = 8'd130;
					y = 7'd16;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score1[2] ? 3'b000 : 3'b111;
				end
				S_SEG_6: begin
					x = 8'd130;
					y = 7'd11;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score1[1] ? 3'b000 : 3'b111;
				end
				S_SEG_7: begin
					x = 8'd131;
					y = 7'd15;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score1[0] ? 3'b000 : 3'b111;
				end
				
				S_SEG_8: begin
					x = 8'd141;
					y = 7'd10;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score2[6] ? 3'b000 : 3'b111;
				end
				S_SEG_9: begin
					x = 8'd145;
					y = 7'd11;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score2[5] ? 3'b000 : 3'b111;
				end
				S_SEG_10: begin
					x = 8'd145;
					y = 7'd16;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score2[4] ? 3'b000 : 3'b111;
				end
				S_SEG_11: begin
					x = 8'd141;
					y = 7'd20;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score2[3] ? 3'b000 : 3'b111;
				end
				S_SEG_12: begin
					x = 8'd140;
					y = 7'd16;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score2[2] ? 3'b000 : 3'b111;
				end
				S_SEG_13: begin
					x = 8'd140;
					y = 7'd11;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd3;
					colour = score2[1] ? 3'b000 : 3'b111;
				end
				S_SEG_14: begin
					x = 8'd141;
					y = 7'd15;
					length = 8'd3;
					x_count = 8'd3;
					y_count = 7'd0;
					colour = score2[0] ? 3'b000 : 3'b111;
				end
				
				default: begin
					x = 8'd0;
					y = 7'd0;
					length = 8'd0;
					x_count = 8'd0;
					y_count = 7'd0;
					colour = 3'b000;
				end
			endcase
			ld_in <= 1'b0;
		end
        else if (enable) begin
			if (x_count == 1'b0 && y_count == 1'b0 && switch < S_SEG_14) begin
				switch = switch + 1'b1;
				ld_in = 1'b1;
			end
			else if (x_count == 1'b0 && y_count == 1'b0 && switch == S_SEG_14) begin
				x_count = 8'd0;
				y_count = 7'd0;
			end
			else if (x_count == 1'b0) begin
				x_count = length;
				y_count = y_count - 1'b1;
			end
			else 
				x_count = x_count - 1'b1;
		end
    end
        
    assign x_out = x + x_count;
    assign y_out = y + y_count;
    
endmodule