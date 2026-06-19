-- Function to send message and update payslip status
CREATE OR REPLACE FUNCTION process_payslip_approval()
RETURNS TRIGGER AS $$
DECLARE
    v_employee_first_name VARCHAR(255);
    v_employee_id VARCHAR(255);
    v_month_year VARCHAR(20);
    v_message TEXT;
BEGIN
    -- Only trigger when status is Pending and we're approving
    IF OLD.status = 'Pending' THEN
        -- Get employee first name and employee ID from employment table
        SELECT e.first_name, emp.employee_id 
        INTO v_employee_first_name, v_employee_id
        FROM employee e
        JOIN employment emp ON e.id = emp.employee_id_ref
        WHERE e.id = NEW.employee_id;

        -- Format month-year string (two-digit month)
        v_month_year := LPAD(NEW.month::TEXT, 2, '0') || '/' || NEW.year;

        -- Create message as per requirements
        v_message := 'Dear ' || v_employee_first_name || ', Your salary of ' || v_month_year || ' from RCA ' || NEW.net_salary || ' has been credited to your ' || v_employee_id || ' account successfully.';

        -- Insert message into message table
        INSERT INTO message (employee_id, message, month_year, sent_at)
        VALUES (NEW.employee_id, v_message, v_month_year, NOW());

        -- Update payslip status to Paid
        NEW.status := 'Paid';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to fire before payslip is updated
CREATE OR REPLACE TRIGGER payslip_approval_trigger
BEFORE UPDATE ON payslip
FOR EACH ROW
EXECUTE FUNCTION process_payslip_approval();
