grammar SpuddX;

/*
	Parser
*/

domain: var_defs
		(all_def)*
		(exec_block)?
		EOF 
		;

var_defs : (var_def)+ ;
var_def : LP 'defvar' var_name LP (var_value)+ RP RP	# RVarDef
		| LP 'defmodelvar' var_name RP 				  	# ModelVarDef
		;
		   
all_def : model_name			# PreDefModel
		| dd_def 				# DDDef
		| pomdp_def 			# POMDPDef
		| ipomdp_def 			# IPOMDPDef
		| dbn_def				# DBNDef
		| pbvi_solv_def 		# PBVISolverDef
		| LP all_def RP 		# OtherDefParen
		;
		 
 
pbvi_solv_def : LP 'defpbvisolv' type=(POMDP | IPOMDP) solv_name RP;

pomdp_def : LP 'defpomdp' model_name
			states_list
			obs_list
			action_var
			action_j_var
			dynamics
			initial_belief
			reward
			discount
			RP
			;

ipomdp_def : LP 'defpomdp' model_name
             states_list
             obs_list
             action_var
             dynamics
             initial_belief
             reward
             discount
             RP
             ;
            
states_list	: LP 'S' LP (var_name)+ RP RP ;
obs_list 	: LP 'O' LP (var_name)+ RP RP ;
action_var 	: LP 'A' (var_name)  RP ;
actions_list 	: LP 'A' LP (var_name)+ RP RP ;

dynamics : LP 'dynamics' (action_model)+ RP ;
action_model : LP action_name all_def RP ;

initial_belief : LP 'b' dd_expr RP ;

reward : LP 'R' (action_reward)+ RP ;
action_reward : LP action_name dd_expr RP ;
		  
discount : LP 'discount' FLOAT_NUM RP ;

dd_def : LP 'defdd' dd_name dd_expr RP ;

dd_expr : dd_decl 											# AtomicExpr
		| op=(OP_ADD | OP_SUB) term=dd_expr 				# NegExpr
		| LP dd_expr RP										# ParenExpr
		| left=dd_expr op=(OP_MUL | OP_DIV) right=dd_expr	# MultDivExpr
		| left=dd_expr op=(OP_ADD | OP_SUB) right=dd_expr	# AddSubExpr
		;
		
dd_decl : LP var_name (LP var_value dd_expr RP)+ RP 	# DDDecl
		| dd_leaf 										# DDleaf
		| same_dd_decl									# SameDD
		| dd_ref										# DDRef
		| var_name var_value 							# DDDeterministic
		| var_name UNIFORM 								# DDUniform
		;
		
dd_ref : dd_name
	   | LP dd_name RP
	   ;

dd_leaf : FLOAT_NUM 
		| LP FLOAT_NUM RP
		;
		
same_dd_decl : LP 'SAME' var_name RP;

dbn_def : LP 'defdbn' model_name (cpd_def)* RP ;
cpd_def : LP var_name dd_expr RP ;

exec_block : LP 'run' (exec_expr)* RP;
exec_expr : dd_name '=' dd_expr 	# DDExecExpr
		  | solv_cmd 				# SolvExpr
		  | LP exec_expr RP 		# ParenExecExpr
		  ;

solv_cmd : 'solve' solv_name model_name backups exp_horizon;

backups : FLOAT_NUM ;
exp_horizon : FLOAT_NUM ;

env_name : IDENTIFIER ;
action_name : IDENTIFIER ;
model_name : IDENTIFIER ;
dd_name : IDENTIFIER;
var_name : IDENTIFIER;
var_value : IDENTIFIER ;
solv_name : IDENTIFIER;

ENV : 'ENV' | 'env' ;
DD : 'DD' | 'dd' ;
POMDP : 'POMDP' | 'pomdp' ;
IPOMDP : 'IPOMDP' | 'ipomdp' ;
DBN : 'DBN' | 'dbn' ;
UNIFORM : 'uniform' | 'UNIFORM' ;
OP_ADD : '+' ;
OP_SUB : '-' ;
OP_MUL : '*' ;
OP_DIV : '/' ;

IDENTIFIER: [_]?[a-zA-Z][a-zA-Z0-9_']* ;
FLOAT_NUM: [0-9]*['.']?[0-9]+ ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;