module finalproj(
		input CLOCK_50, 
        input [1:0] KEY,
		// The ports below are for the VGA output.  Do not change.
		output VGA_CLK, VGA_HS, VGA_VS, VGA_BLANK_N, VGA_SYNC_N, 
		output [9:0] VGA_R, VGA_G, VGA_B
	);
	
	wire resetn;
	assign resetn = KEY[0];
	
	wire writeEn;
	wire [7:0] x_out;
	wire [6:0] y_out;
	wire [2:0] colour_out;
	
	
	vga_adapter VGA(
			.resetn(resetn),
			.clock(CLOCK_50),
			.colour(colour_out),
			.x(x_out),
			.y(y_out),
			.plot(writeEn),
			.VGA_R(VGA_R),
			.VGA_G(VGA_G),
			.VGA_B(VGA_B),
			.VGA_HS(VGA_HS),
			.VGA_VS(VGA_VS),
			.VGA_BLANK(VGA_BLANK_N),
			.VGA_SYNC(VGA_SYNC_N),
			.VGA_CLK(VGA_CLK)
		);
		defparam VGA.RESOLUTION = "160x120";
		defparam VGA.MONOCHROME = "FALSE";
		defparam VGA.BITS_PER_COLOUR_CHANNEL = 1;
		defparam VGA.BACKGROUND_IMAGE = "black.mif";
			
	wire en_plot, en_wait, reset_plot, reset_wait, f_plot, f_wait;
	wire en_move;
	
	ratedivider r1(en_plot, 24'd5000, CLOCK_50, reset_plot, f_plot);
	ratedivider r3(en_wait, 24'd2489995, CLOCK_50, reset_wait, f_wait);
	
	wire fly;
	assign fly = ~KEY[1];
	
	wire [6:0] bird_y, gap_1_y, gap_2_y, gap_3_y;
	wire [7:0] pillar_1_x, pillar_2_x, pillar_3_x;
	
	bird_pos b(
		CLOCK_50, resetn, game_reset, en_move,
		fly,
		bird_y
	);
	
	wire ld_erase, ld_sw, ld_rs;
	
	wire [3:0] rand_gap_1, rand_gap_2, rand_gap_3;
	
	randomgenerator rg1(CLOCK_50, resetn, 4'd13, rand_gap_1);
	randomgenerator rg2(CLOCK_50, resetn, 4'd8, rand_gap_2);
	randomgenerator rg3(CLOCK_50, resetn, 4'd2, rand_gap_3);
	
	pillar_pos p1(
		CLOCK_50, resetn, game_reset, en_move,
		8'd61,
		rand_gap_1,
		pillar_1_x, 
		gap_1_y
	);
	
	pillar_pos p2(
		CLOCK_50, resetn, game_reset, en_move,
		8'd122,
		rand_gap_2,
		pillar_2_x, 
		gap_2_y
	);
	
	pillar_pos p3(
		CLOCK_50, resetn, game_reset, en_move,
		8'd183,
		rand_gap_3,
		pillar_3_x, 
		gap_3_y
	);
		
	wire plot; 
	wire [6:0] digit1, digit2;
	
	scoreboard scb(
		CLOCK_50, resetn, game_reset, en_move,
		pillar_1_x, pillar_2_x, pillar_3_x,
		bird_y, gap_1_y, gap_2_y, gap_3_y,
		digit1, digit2
	);
	
	
	datapath dp(
        CLOCK_50, resetn,
        pillar_1_x, pillar_2_x, pillar_3_x,  
		bird_y, gap_1_y, gap_2_y, gap_3_y, 
		digit1, digit2,
        ld_erase, ld_sw, ld_rs, writeEn,
        x_out,
        y_out,
        colour_out
    );
	
	wire fail; 
	wire game_reset;
	
	control c(
		CLOCK_50, resetn, fly, 
		f_plot, f_wait, fail, 
		en_wait, en_plot,
		en_move,
		ld_sw, ld_erase, ld_rs, 
		reset_wait, reset_plot,
		writeEn, game_reset
    );
	
	collision_detection cd(
		CLOCK_50, resetn, game_reset, 
		pillar_1_x, pillar_2_x, pillar_3_x,
		bird_y, gap_1_y, gap_2_y, gap_3_y, 
		fail
	);
	
endmodule