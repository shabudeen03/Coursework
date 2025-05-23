#include "hw7.h"

bst_sf* insert_bst_sf(matrix_sf *mat, bst_sf *root) {
    bst_sf* prev = NULL;
    bst_sf* curr = root;
    
    while(curr != NULL) {
        prev = curr;

        //If root is greater than mat, then go left child else right child
        if(curr->mat->name - mat->name > 0) {
            curr = curr -> left_child;
        } else {
            curr = curr -> right_child;
        }
    }

    bst_sf* node = malloc(sizeof(bst_sf));
    node -> mat = mat;
    node -> left_child = NULL;
    node -> right_child = NULL;

    //Connect
    if(prev == NULL) {
        root = node;
    } else if(mat->name < prev->mat->name) {
        prev->left_child = node;
    } else {
        prev->right_child = node;
    }

    return root;
}

void free_bst_sf(bst_sf *root) {
    //Need to free children first then parent, postorder traversal from cse 260
    if(root == NULL) {
        return;
    }

    //Left subtree, then right subtree
    free_bst_sf(root->left_child);
    free_bst_sf(root->right_child);

    //Now free the parent, but first the matrix struct
    free(root->mat);
    free(root);
}

matrix_sf* find_bst_sf(char name, bst_sf *root) {
    bst_sf* curr = root;
    while(curr != NULL && curr->mat->name != name) {
        if(curr->mat->name == name) { //If it is the node demanded, return it
            // return curr->mat;
            break;
        } else if(curr->mat->name > name) { //Left child or right child otherwise
            curr = curr -> left_child; 
        } else {
            curr = curr -> right_child;
        }
    }

    if(curr == NULL) {
        return NULL;
    }

    return curr->mat; //Not found in tree
}

matrix_sf* add_mats_sf(const matrix_sf *mat1, const matrix_sf *mat2) {
    matrix_sf* m = malloc(sizeof(matrix_sf) + mat1->num_rows * mat1->num_cols * sizeof(int));

    m->name = '?'; //Saw this in Copy so just kept
    m->num_rows = mat1->num_rows;
    m->num_cols = mat1->num_cols;

    for(int i=0; i<m->num_rows * m->num_cols; i++) {
        m->values[i] = mat1->values[i] + mat2->values[i];
    }

    return m;
}

matrix_sf* mult_mats_sf(const matrix_sf *mat1, const matrix_sf *mat2) {
    matrix_sf* m = malloc(sizeof(matrix_sf) + mat2->num_cols * mat1->num_rows * sizeof(int));

    m->name = '?';
    m->num_rows = mat1->num_rows;
    m->num_cols = mat2->num_cols;

    for(int i=0; i<m->num_cols * m->num_rows; i++) {
        m->values[i] = 0;
    }

    for(int i=0; i<m->num_rows; i++) {
        for(int j=0; j<m->num_cols; j++) {
            for(int k=0; k<mat1->num_cols; k++) {
                m->values[i*m->num_cols + j] += mat1->values[i*mat1->num_cols + k] * mat2->values[k*mat2->num_cols + j];
            }
        }
    }

    return m;
}

matrix_sf* transpose_mat_sf(const matrix_sf *mat) {
    matrix_sf* m = malloc(sizeof(matrix_sf) + mat->num_rows * mat->num_cols * sizeof(int));

    m->name = '?'; //Saw this in Copy so just kept for now
    m->num_rows = mat->num_cols;
    m->num_cols = mat->num_rows;

    for(int i=0; i<mat->num_rows; i++) {
        for(int j=0; j<mat->num_cols; j++) {
            //Figure this out in 1d
            m->values[j*mat->num_rows + i] = mat->values[i*mat->num_cols + j];
        }
    }

    return m;
}
  
// Code from tutorial modified
int precedence(char operator) {
    switch (operator) {
        case '+':
            return 1;
        case '*':
            return 2;
        case '\'':
            return 3;
        default:
            return -1;
    }
}
  
// Code from tutorial modified
int isOperator(char ch) {
    return (ch == '+' || ch == '*' || ch == '\'');
}
  
// Code from tutorial modified
char* infix2postfix_sf(char* infix) {
    int i, j;
    int len = strlen(infix);
    char* postfix = (char*)malloc(sizeof(char) * (len + 2));
    char stack[MAX_LINE_LEN];
    int top = -1;
  
    for (i = 0, j = 0; i < len; i++) {
        if (infix[i] == ' ' || infix[i] == '\t')
            continue;
        
        // If the scanned character is operand
        // add it to the postfix expression
        if (isalnum(infix[i])) {
            postfix[j++] = infix[i];
        }
        
        // if the scanned character is '('
        // push it in the stack
        else if (infix[i] == '(') {
            stack[++top] = infix[i];
        }
        
        // if the scanned character is ')'
        // pop the stack and add it to the 
        // output string until empty or '(' found
        else if (infix[i] == ')') {
            while (top > -1 && stack[top] != '(')
                postfix[j++] = stack[top--];
            if (top > -1 && stack[top] != '(')
                return "Invalid Expression";
            else
                top--;
        }
        
        // If the scanned character is an operator
        // push it in the stack
        else if (isOperator(infix[i])) {
            while (top > -1
                   && precedence(stack[top])
                          >= precedence(infix[i]))
                postfix[j++] = stack[top--];
            stack[++top] = infix[i];
        }
    }
  
    // Pop all remaining elements from the stack
    while (top > -1) {
        if (stack[top] == '(') {
            return "Invalid Expression";
        }
        postfix[j++] = stack[top--];
    }
    postfix[j] = '\0';
    return postfix;
}

matrix_sf* evaluate_expr_sf(char name, char *expr, bst_sf *root) {
    for(int i=0; i<strlen(expr); i++) {
        if(expr[i] == '=') {
            expr += i + 1;
            break;
        }
    }

    char *postfix = infix2postfix_sf(expr);
    // printf("%s\n", postfix);
    matrix_sf* m = NULL;

    int count = 0;
    for(int i=0; i<strlen(postfix); i++) {
        if(!isOperator(postfix[i])) {
            count++;
        }
    }

    matrix_sf* list[count];
    int idx = -1;
    for(int i=0; i<strlen(postfix); i++) {
        if(isalpha(postfix[i])) {
            list[++idx] = find_bst_sf(postfix[i], root);
        } else {
            if(postfix[i] == '\'') {
                matrix_sf* a = list[idx];

                m = transpose_mat_sf(a);

                if(a->name == '?') {
                    free(a);
                }
            } else {
                matrix_sf* b = list[idx--];
                matrix_sf* a = list[idx];

                if(postfix[i] == '+') {
                    m = add_mats_sf(a, b);
                } else {
                    m = mult_mats_sf(a, b);
                }

                if(a->name == '?') {
                    free(a);
                }

                if(b->name == '?') {
                    free(b);
                }
            }

            list[idx] = m;
        }
    }

    free(postfix);
    m->name = name;
    return m;
}

matrix_sf* create_matrix_sf(char name, const char *expr) {
    int idx = 0; 
    int numRows = 0, numCols = 0, val = 0; 

    if(!isdigit(expr[idx])) {
        while(!isdigit(expr[++idx])); //Skip to NR
    }

    sscanf(expr + idx, "%d", &numRows); //(idx++)
    //numRows could be 1 digit or 2+ digits

    while(isdigit(expr[++idx]));
    // if(numRows > 9) while(isdigit(expr[idx++])); //Skip NR Digits

//NEed to handle NC AND NR PROPERLY THEN I AM FINISHED
    while(isspace(expr[++idx])); //Skip to NC
    sscanf(expr + idx, "%d", &numCols); //(idx++)
    while(isdigit(expr[idx++])); //Skip NC Digits

    matrix_sf* m = malloc(sizeof(matrix_sf) + numRows * numCols * sizeof(int));
    m->name = name;
    m->num_cols = numCols;
    m->num_rows = numRows;

    int arrIdx = 0;
    int valueRead = 0;
    int totalValues = numRows * numCols;
    while(valueRead < totalValues) {
        while(!isdigit(expr[idx]) && expr[idx] != '-') {
            idx++; //Skip to a number
        }

        sscanf(expr + idx, "%d", &val);
        while(isdigit(expr[idx]) || expr[idx] == '-') {
            idx++;
        }

        m->values[arrIdx++] = val;
        valueRead++;
    }

    return m;
}

matrix_sf *execute_script_sf(char *filename) {
    FILE *file = fopen(filename, "r");

    int formula = 0; //0 for creating matrix, 1 for math expression
    char *line = malloc(MAX_LINE_LEN);
    bst_sf* root = NULL;
    matrix_sf* m = NULL;

    while(fgets(line, MAX_LINE_LEN, file) != NULL) {
        m = NULL;
        formula = 0;

        for(int i=0; i<strlen(line); i++) {
            if(isOperator(line[i])) {
                formula = 1;
                break;
            }
        }

        if(formula == 0) {
            m = create_matrix_sf(line[0], line);
        } else {
            m = evaluate_expr_sf(line[0], line, root);
        }

        // print_matrix_sf(m);
        //insert into bst, do bst stuff later
        if(m != NULL) {
            // fgets(line, MAX_LINE_LEN, file);

            // if(line != NULL && strlen(line) > 1) {
            //     root = insert_bst_sf(m, root);
            // }

            // int length = strlen(line);
            // fseek(file, - (length - 1), SEEK_CUR);

            // print_matrix_sf(m);
            root = insert_bst_sf(m, root);
        }
    }

    matrix_sf* toReturn = copy_matrix(m->num_rows, m->num_cols, m->values);
    free(line);
    free_bst_sf(root);
    fclose(file);
    return toReturn;
}

// This is a utility function used during testing. Feel free to adapt the code to implement some of
// the assignment. Feel equally free to ignore it.
matrix_sf *copy_matrix(unsigned int num_rows, unsigned int num_cols, int values[]) {
    matrix_sf *m = malloc(sizeof(matrix_sf)+num_rows*num_cols*sizeof(int));
    m->name = '?';
    m->num_rows = num_rows;
    m->num_cols = num_cols;
    memcpy(m->values, values, num_rows*num_cols*sizeof(int));
    return m;
}

// Don't touch this function. It's used by the testing framework.
// It's been left here in case it helps you debug and test your code.
void print_matrix_sf(matrix_sf *mat) {
    assert(mat != NULL);
    assert(mat->num_rows <= 1000);
    assert(mat->num_cols <= 1000);
    printf("%d %d ", mat->num_rows, mat->num_cols);
    for (unsigned int i = 0; i < mat->num_rows*mat->num_cols; i++) {
        printf("%d", mat->values[i]);
        if (i < mat->num_rows*mat->num_cols-1)
            printf(" ");
    }
    printf("\n");
}
